package com.os467.lib.yaml;

import com.os467.lib.AnnotationInjector;
import com.os467.lib.DefaultClassScanner;
import com.os467.lib.singleton.SingletonFactory;
import com.os467.lib.annotation.yamlConfig.YamlConfigInjection;
import com.os467.lib.exception.ConfigInjectException;

import java.lang.reflect.Field;

public class YamlObjectInjector implements AnnotationInjector {

    private DefaultYamlConfigFactory yamlObjectFactory;

    public YamlObjectInjector(DefaultYamlConfigFactory yamlObjectFactory) {
        this.yamlObjectFactory = yamlObjectFactory;
    }

    /**
     * 为指定对象注入配置
     * @param obj
     */
    public void inject(Object obj) {
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            YamlConfigInjection injectAnnotation = declaredField.getDeclaredAnnotation(YamlConfigInjection.class);
            if (injectAnnotation != null){
                //准备获取实例
                //获取实例名
                String value = injectAnnotation.value();
                if (value.length() > 0){
                    yamlObjectFactory.getProductByName(value);
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

}
