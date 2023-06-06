package com.os467.lib;


import com.os467.lib.yaml.ConfigResources;
import com.os467.lib.yaml.DefaultYamlConfigFactory;
import com.os467.lib.yaml.YamlObjectInjector;
import com.os467.lib.yaml.YamlReader;

public class ConfigUtils {

    //创建一个读取器
    private final static YamlReader yamlReader = new YamlReader();

    //创建一个配置对象工厂
    private final static DefaultYamlConfigFactory configObjectFactory = new DefaultYamlConfigFactory();

    //创建一个资源配路径列表
    private final static ConfigResources configResources = new ConfigResources();


    /**
     * 为此类所有被 @YamlConfigInjection 注解标注的属性注入配置对象实例，若配置对象不存则不做注入操作
     * 若不指定注入配置名称，则默认按照接口或类类型进行匹配，必须保证配置实例唯一性
     * 也就是被 @YamlConfigValue
     * @param mainClass
     * @param <T>
     * @return
     */
    public static <T>T config(Class<T> mainClass){
        prepare();
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


    public static ConfigResources addResources(String configPath){
        configResources.addResources(configPath);
        return configResources;
    }

    /**
     * 配置准备工作
     */
    private static void prepare() {
        yamlReader.registerConfigResources(configResources);
        //将读取结果交由工厂处理，生成实例
        configObjectFactory.produce(yamlReader.readConfig());
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
        return (T)configObjectFactory.getProductByName(configName);
    }

}
