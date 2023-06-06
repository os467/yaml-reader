package com.os467.lib.yaml;

import java.util.ArrayList;
import java.util.List;

public class ConfigResources {

    private static String WORK_DIRECTORY = System.getProperty("usr.dir") + "\\resources\\config";

    //资源列表
    private static List<String> configResources;

    public ConfigResources addResources(String configPath){
        if (configResources == null){
            configResources = new ArrayList<>();
        }
        if (configPath.indexOf(0) == '.' && configPath.length() > 1){
            configPath = WORK_DIRECTORY + configPath.substring(configPath.indexOf('.')+1);
        }
        configResources.add(configPath);
        return this;
    }

    public List<String> getResources() {
        return configResources;
    }
}
