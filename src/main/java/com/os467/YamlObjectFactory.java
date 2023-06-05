package com.os467;


import com.os467.annotation.yamlConfig.YamlConfigValue;
import com.os467.exception.ConfigEventNotFoundException;
import com.os467.exception.ConfigInjectException;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Yaml配置对象工厂
 */
public class YamlObjectFactory implements ConfigObjectFactory {

    private Map<String,YamlConfigEvent> rootMap;

    private YamlConfigEvent event;

    private ClassScanner classScanner = SingletonFactory.classScanner;

    //工厂仓库
    private static Map<String,Object> wareHouse = new HashMap<>();

    public void produce(Map rootMap) {
        this.rootMap = rootMap;
        produce();
    }

    @Override
    public Object getProduct(String name) {
        return wareHouse.get(name);
    }

    @Override
    public Object getProductByClassName(String className) {
        return getOnlyOneClass(className);
    }

    @Override
    public Object getProductByInterfaceName(String interfaceName) {
        return getOnlyOneInterface(interfaceName);
    }

    /**
     * 返回唯一类实例
     * @param className
     * @return
     */
    private Object getOnlyOneClass(String className) {
        return getObject(className);
    }

    /**
     * 返回唯一接口实例
     * @param interfaceName
     * @return
     */
    private Object getOnlyOneInterface(String interfaceName) {
        return getObject(interfaceName);
    }

    private Object getObject(String name) {
        Class<?> clazzClass = null;
        Object ret = null;
        try {
            clazzClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        int time = 0;
        Iterator<Map.Entry<String, Object>> iterator = wareHouse.entrySet().iterator();
        while (iterator.hasNext()) {
            //父类型引用，只能存在唯一的子类或父类实例
            Map.Entry<String, Object> entry = iterator.next();
            Class<?> aClass = entry.getValue().getClass();
            if (clazzClass.isAssignableFrom(aClass)){
                time++;
                ret = entry.getValue();
            }
        }
        if (time != 1){
            throw new ConfigInjectException("类或接口配置实例不唯一");
        }
        return ret;
    }

    private void produce() {
        List<String> classNameList = classScanner.getClassNameList();
        for (int i = 0; i < classNameList.size(); i++) {
            try {
                Class<?> aClass = Class.forName(classNameList.get(i));
                YamlConfigValue configValue = aClass.getDeclaredAnnotation(YamlConfigValue.class);
                if (configValue != null){
                    String rootEventName = configValue.value();
                    //获取到对应的根配置
                    event = rootMap.get(rootEventName);
                    if (event == null){
                        throw new ConfigEventNotFoundException(rootEventName);
                    }
                    if (wareHouse.get(rootEventName) == null){
                        //创建Yaml配置对象，注入依赖，存入仓库
                        Object object = createObject(aClass, event);
                        wareHouse.put(rootEventName,object);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据泛型类型名称判断类型
     * 接口会带有interface关键字
     * 类类型会带有class关键字
     * 基本数据类型 对应其本身关键字
     * 带泛型的类型，没有class或是interface关键字
     * @param objClass
     * @param fatherEvent
     * @return
     */
    private Object createObject(Class objClass, YamlConfigEvent fatherEvent) {
        Object obj = null;
        try {
            obj = objClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //属性字节码列表
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        Type genericType;
        for (int i = 0; i < declaredFields.length; i++) {
            YamlConfigValue fieldValue = declaredFields[i].getDeclaredAnnotation(YamlConfigValue.class);
            if (fieldValue != null){
                //注入依赖
                genericType = declaredFields[i].getGenericType();

                //获取属性全类名(清除class标记)
                String fieldClassName = genericType.toString().replace("class ", "");

                //获取到注入的实例
                Object injectObj = getInjectObj(fatherEvent, fieldValue.value(), fieldClassName);

                //打破封装
                declaredFields[i].setAccessible(true);
                //注入
                try {
                    declaredFields[i].set(obj,injectObj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 获取到注入实例的方法
     * 如果是一个常用数据类型，则返回的常用数据类型值不为null
     * 交由
     * @param fatherEvent
     * @param fieldValue
     * @param fieldClassName
     * @return
     */
    private Object getInjectObj(YamlConfigEvent fatherEvent, String fieldValue, String fieldClassName) {
        //被注入的实例
        Object injectObj;

        //实例配置的值
        String childEventValue = fatherEvent.getChildEventValue(fieldValue);
        //常用数据类型创建
        injectObj = selectType(fieldClassName, childEventValue);
        if (injectObj == null) {
            //创建复杂数据类型
            List<String> childEventValues = fatherEvent.getChildEventValues(fieldValue);
            if (childEventValues != null) {
                //复杂数据类型创建
                injectObj = selectType(fieldClassName, childEventValues);
            }

            //创建类类型
            if (injectObj == null) {
                //创建类类型，该类字节码需要存在
                Class<?> eventClass = null;
                try {
                    eventClass = Class.forName(fieldClassName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                YamlConfigEvent childEvent = fatherEvent.getChildEvent(fieldValue);
                injectObj = createObject(eventClass, childEvent);
            }
        }
        return injectObj;
    }


    /**
     * 复杂数据类型选择并创建实例
     * @param fieldClassName
     * @param childEventValues
     * @return
     */
    private Object selectType(String fieldClassName,List<String> childEventValues){
        Object injectObj = null;
        int s = fieldClassName.indexOf("<");
        if (s == -1){
            //无泛型
            //去除接口关键字
            fieldClassName = fieldClassName.replace("interface ","");
            switch (fieldClassName){
                case "java.util.List":{
                    //默认为ArrayList
                    List objects = new ArrayList<>();
                    for (int i = 0; i < childEventValues.size(); i++) {
                        objects.add(childEventValues.get(i));
                    }
                    injectObj = objects;
                    break;
                }
                case "java.util.Map":{
                    //默认为哈希集合
                    Map hashMap = new HashMap();
                    for (int i = 0; i < childEventValues.size(); i++) {
                        hashMap.put(i,childEventValues.get(i));
                    }
                    injectObj = hashMap;
                    break;
                }
            }
        }else {
            //存在泛型
            int e = fieldClassName.indexOf(">");
            //获取到泛型名
            String genericName = fieldClassName.substring(s+1,e);
            fieldClassName = fieldClassName.substring(0,s);
            switch (fieldClassName){
                case "java.util.List":{
                    //默认为ArrayList
                    List objects = new ArrayList<>();
                    for (int i = 0; i < childEventValues.size(); i++) {
                        objects.add(selectType(genericName,childEventValues.get(i)));
                    }
                    injectObj = objects;
                    break;
                }
                case "java.util.Map":{
                    //默认为哈希集合
                    Map hashMap = new HashMap();
                    String[] keyAndValueGenericName = genericName.split(",");
                    String keyGenericName = keyAndValueGenericName[0];
                    String valueGenericName = keyAndValueGenericName[1].replace(" ","");
                    for (int i = 0; i < childEventValues.size(); i++) {
                        hashMap.put(selectType(keyGenericName,String.valueOf(i)),selectType(valueGenericName,childEventValues.get(i)));
                    }
                    injectObj = hashMap;
                    break;
                }
            }
        }
        return injectObj;
    }

    /**
     * 选择常用数据类型并创建实例
     * @param fieldClassName
     * @param childEventValue
     * @return
     */
    private Object selectType(String fieldClassName, String childEventValue) {
        Object injectObj = null;
        //常用数据类型
        switch (fieldClassName){
            case "java.lang.Boolean":
            case "boolean": {
                injectObj = new Boolean(childEventValue);
                break;
            }
            case "java.lang.Integer":
            case "int": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Integer(0);
                }else {
                    injectObj = new Integer(childEventValue);
                }
                break;
            }
            case "java.lang.Double":
            case "double": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Double(0);
                }else {
                    injectObj = new Double(childEventValue);
                }
                break;
            }
            case "java.lang.Long":
            case "long": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Long(0);
                }else {
                    injectObj = new Long(childEventValue);
                }
                break;
            }
            case "java.lang.Byte":
            case "byte": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Byte((byte) (0));
                }else {
                    injectObj = new Byte(childEventValue);
                }
                break;
            }
            case "java.lang.Short":
            case "short": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Short((short) (0));
                }else {
                    injectObj = new Short(childEventValue);
                }
                break;
            }
            case "java.lang.Float":
            case "float": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Float((float) (0));
                }else {
                    injectObj = new Float(childEventValue);
                }
                break;
            }
            case "java.lang.Character":
            case "char": {
                if (childEventValue == null || childEventValue.length() == 0){
                    injectObj = new Character((char) (0));
                }else {
                    injectObj = new Character(childEventValue.charAt(0));
                }
                break;
            }
            case "java.lang.String":
            {
                injectObj = childEventValue;
                break;
            }
        }
        return injectObj;
    }
}
