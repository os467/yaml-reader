package com.os467;


import com.os467.annotation.SingletonInjection;
import com.os467.annotation.SingletonObject;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 第一次扫描所有被@SingletonObject注解标注的类，生成类的半成品实例。
 *
 * 首先半成品实例会去缓存中获取其需要的实例，若该实例尚未被创建，则需要为该实例注册一个需求清单，
 * 在需求清单中存放当前半成品类名。
 *
 * 检查该实例是否存在需求清单，若存在，则需要从缓存中获取到需求清单上的半成品实例。
 * 为获取到的半成品实例装配此实例，然后从需求清单上移除此项，直到解决完清单所有需求。
 * 最后将当前半成品放入缓存中。
 *
 *
 * The first scan of all classes annotated with the @SingletonObject annotation
 * generates a half-finished instance of the class.
 *
 * First the semi-finished instance goes to the cache to get the instance it needs,
 * and if that instance has not been created yet, a requirement list is registered
 * for that instance.
 * In the requirements list the current semi-finished class name is stored.
 *
 * Check if the requirement list exists for the instance, and if so,
 * fetch the semi-finished instance from the cache to the requirement list.
 * Assemble this instance for the obtained semi-finished instance and then
 * remove this item from the requirements list until all requirements
 * in the list have been resolved.
 *
 * Finally the current semi-finished product is placed in the cache.
 *
 */
public class SingletonFactory {

    //First Scan produce all @Singleton object's half product
    //half product register a list of the Objects that it needs but haven't create and put it to the map
    //Product -> List<half Product> event in the map
    //if create the next Product ,we need check the list,give this product's instance reference to the needed Product on the list
    //remove the Product -> List<half Product> event in the map

    public static ClassScanner classScanner = new ClassScanner();

    private static Map productCache = new HashMap();

    private static Map<String,List<String>> referenceMap = new HashMap();

    public static void produce(){
        List<String> classNameList = classScanner.getClassNameList();
        for (String productClassName : classNameList) {
            try {
                Class<?> aClass = Class.forName(productClassName);
                SingletonObject singletonObject = aClass.getDeclaredAnnotation(SingletonObject.class);
                //此类为需要被创建的单例
                if (singletonObject != null){
                    try {
                        Object o = aClass.newInstance();
                        //创建后放入缓存
                        productCache.put(productClassName,o);
                        //检查属性
                        Field[] declaredFields = aClass.getDeclaredFields();
                        for (Field declaredField : declaredFields) {
                            SingletonInjection singletonInjection = declaredField.getDeclaredAnnotation(SingletonInjection.class);
                            if (singletonInjection != null){
                                //去缓存中获取引用对象(全类名查找)
                                String typeName = declaredField.getGenericType().getTypeName();
                                Object need = productCache.get(typeName);
                                if (need != null){
                                    declaredField.setAccessible(true);
                                    declaredField.set(o,need);
                                }else {
                                    //在引用清单中添加此类型的新引用对象全类名
                                    List<String> referenceList = referenceMap.get(typeName);
                                    if (referenceList == null){
                                        referenceList = new ArrayList<>();
                                    }
                                    referenceList.add(o.getClass().getName());
                                    //返回缓存
                                    referenceMap.put(typeName,referenceList);
                                }
                            }
                        }
                        //检查被引用的清单
                        List<String> referenceList = referenceMap.get(productClassName);
                        if (referenceList != null){
                            Iterator<String> iterator = referenceList.iterator();
                            while (iterator.hasNext()) {
                                String referenceClassName = iterator.next();
                                //遍历引用清单，去缓存中取引用对象
                                Object referenceProduct = productCache.get(referenceClassName);
                                //遍历引用对象属性
                                Field[] declaredField = referenceProduct.getClass().getDeclaredFields();
                                for (Field field : declaredField) {
                                    //此属性与当前对象匹配
                                    if (field.getGenericType().getTypeName().equals(productClassName)){
                                        field.setAccessible(true);
                                        field.set(referenceProduct,o);
                                        //清除该需求项
                                        iterator.remove();
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static <T>T getSingletonObject(Class<T> aClass) {
        return (T)productCache.get(aClass.getName());
    }
}
