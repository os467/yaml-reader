package com.os467;

import com.os467.annotation.yamlConfig.YamlConfigInjection;
import com.os467.exception.ConfigInjectException;

import java.lang.reflect.Field;

public class YamlObjectInjector implements AnnotationInjector{

    private ClassScanner classScanner = SingletonFactory.classScanner;

    private ConfigObjectFactory yamlObjectFactory;

    public YamlObjectInjector(ConfigObjectFactory yamlObjectFactory) {
        this.yamlObjectFactory = yamlObjectFactory;
    }

    public void inject(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            YamlConfigInjection injectAnnotation = declaredField.getDeclaredAnnotation(YamlConfigInjection.class);
            if (injectAnnotation != null){
                //准备获取实例
                //获取实例名
                String value = injectAnnotation.value();
                if (value.length() > 0){
                    yamlObjectFactory.getProduct(value);
                }else {
                    //获取对应类型
                    String genericType = declaredField.getGenericType().toString();
                    Object product;
                    if (genericType.contains("class")){
                        product = yamlObjectFactory.getProductByClassName(genericType.replace("class ", ""));
                    }else if (genericType.contains("interface")){
                        product = yamlObjectFactory.getProductByInterfaceName(genericType.replace("interface ", ""));
                    }else {
                        throw new ConfigInjectException("未知注入类型");
                    }
                    //获取到实例对象
                    //打破封装
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(obj,product);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void inject() {
   /*     List<String> classNameList = classScanner.getClassNameList();
        for (String className : classNameList) {
            try {
                Class<?> aClass = Class.forName(className);
                Field[] declaredFields = aClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    YamlConfigInjection injectAnnotation = declaredField.getDeclaredAnnotation(YamlConfigInjection.class);
                    if (injectAnnotation != null){
                        //准备获取实例
                        //获取实例名
                        String value = injectAnnotation.value();
                        if (value.length() > 0){
                            yamlObjectFactory.getProduct(value);
                        }else {
                            //获取对应类型
                            String genericType = declaredField.getGenericType().toString();
                            Object product;
                            if (genericType.contains("class")){
                                product = yamlObjectFactory.getProductByClassName(genericType.replace("class ", ""));
                            }else if (genericType.contains("interface")){
                                product = yamlObjectFactory.getProductByInterfaceName(genericType.replace("interface ", ""));
                            }else {
                                throw new ConfigInjectException("未知注入类型");
                            }
                            if (product == null){
                                throw new ConfigInjectException("类或接口配置实例不唯一");
                            }
                            //获取到实例对象
                            //todo
                            //打破封装
                            declaredField.setAccessible(true);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }*/
    }
}
