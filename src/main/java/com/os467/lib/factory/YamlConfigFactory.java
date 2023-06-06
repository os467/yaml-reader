package com.os467.lib.factory;

import java.util.Map;

public interface YamlConfigFactory extends ObjectFactory {

    /**
     * Generate configuration objects based on the configuration data in the collection
     * @param configMap
     */
    void produce(Map configMap);

    /**
     * Obtain products based on user-defined product names.
     * @param name
     * @return
     */
    Object getProductByName(String name);

    /**
     * Obtain the product based on its full class name.
     * @param className
     * @return
     */
    Object getProductByClassName(String className);

    /**
     * Obtain the product based on the interface name implemented by the product.
     * @param interfaceName
     * @return
     */
    Object getProductByInterfaceName(String interfaceName);

}
