package com.os467;

import java.util.Map;

public interface ConfigObjectFactory {

    /**
     * Generate configuration objects based on the configuration data in the collection
     * @param rootMap
     */
    void produce(Map rootMap);

    Object getProduct(String name);

    Object getProductByClassName(String className);

    Object getProductByInterfaceName(String interfaceName);
}
