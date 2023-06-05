package com.os467;


import com.os467.testEntity.RootConfig;

import java.util.Map;

public class ConfigUtils {


    private final static YamlReader yamlReader = new YamlReader();

    private final static ConfigObjectFactory configObjectFactory = new YamlObjectFactory();

    static {
        //将读取结果交由工厂处理，生成实例
        configObjectFactory.produce(yamlReader.readConfig());
    }

    /**
     * 为此类所有被 @YamlConfigInjection 注解标注的属性注入配置对象实例，若配置对象不存则不做注入操作
     * 若不指定注入配置名称，则默认按照接口或类类型进行匹配，必须保证配置实例唯一性
     * 也就是被 @YamlConfigValue
     * @param mainClass
     * @param <T>
     * @return
     */
    public static <T>T config(Class<T> mainClass){
        T o = null;
        try {
            o = mainClass.newInstance();
            //创建一个实例注入器
            YamlObjectInjector yamlObjectInjector = new YamlObjectInjector(configObjectFactory);
            yamlObjectInjector.inject(o);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 通过类的字节码获取到配置类实例
     * @param rootConfigClass
     * @param <T>
     * @return
     */
    public static <T>T getConfig(Class<T> rootConfigClass) {
        return (T)configObjectFactory.getProductByClassName(rootConfigClass.getName());
    }

    public static <T>T getConfig(String configName,Class<T> rootConfigClass){
        return (T)configObjectFactory.getProduct(configName);
    }

}
