package com.os467;

import com.os467.exception.ConfigEventNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlConfigEvent implements ConfigEvent {

    //父配置项
    private YamlConfigEvent father;

    //yaml配置项层级
    private Integer level;

    //yaml配置名
    private String name;

    //yaml配置值,若不存在值则为null
    private String value;

    //yaml配置值，数组类型
    private List<String> values;

    //若存在子项配置则启用
    private Map<String,YamlConfigEvent> children;

    public YamlConfigEvent(YamlConfigEvent father, Integer level, String name, String value, Map<String, YamlConfigEvent> children) {
        this.father = father;
        this.level = level;
        this.name = name;
        this.value = value;
        this.children = children;
    }

    public YamlConfigEvent getFather() {
        return father;
    }

    public void setFather(YamlConfigEvent father) {
        this.father = father;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, YamlConfigEvent> getChildren() {
        return children;
    }

    public void setChildren(Map<String, YamlConfigEvent> children) {
        this.children = children;
    }

    public void addChildEvent(YamlConfigEvent child) {
        if (children == null){
            children = new HashMap<>();
        }
        children.put(child.name, child);
    }

    public void addValue(String value) {
        if (this.value == null){
            this.value = value;
        }
    }

    /**
     * 获取配置项
     * @param eventName
     * @return
     */
    public YamlConfigEvent getChildEvent(String eventName) {
        YamlConfigEvent yamlConfigEvent = children.get(eventName);
        if (yamlConfigEvent == null){
            throw new ConfigEventNotFoundException("没有此配置: "+ getPath() + eventName);
        }
        return yamlConfigEvent;
    }

    /**
     * 获取值 复杂数据类型配置
     * @param eventName
     * @return
     */
    public List<String> getChildEventValues(String eventName) {
        YamlConfigEvent childEvent = children.get(eventName);
        if (childEvent == null){
            throw new ConfigEventNotFoundException("没有此配置: "+ getPath() + eventName);
        }
        return childEvent.values;
    }

    /**
     * 获取值 常见数据类型配置
     * @param eventName
     * @return
     */
    public String getChildEventValue(String eventName) {
        YamlConfigEvent childEvent = children.get(eventName);
        if (childEvent == null){
            throw new ConfigEventNotFoundException("没有此配置: "+ getPath() + eventName);
        }
        return childEvent.value;
    }

    private String getPath() {
        if (father == null){
            return name + ":";
        }
        return father.getPath() + name + ":";
    }

    /**
     * 保存数组值
     * @param value
     */
    public void addToValues(String value) {
        if (values == null){
            values = new ArrayList<>();
        }
        if (value.length() > 1){
            if (value.charAt(0) == ' '){
                value = value.substring(1);
            }
        }
        values.add(value);
    }


}
