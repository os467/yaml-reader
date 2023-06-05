# yaml-reader
A project that implements automatic reading of Yaml configuration files and provides instance dependency injection


```yaml
root:
  list:
    - 1
    - 2
    - 3
  map:
    - true
    - false
    - false
  boolean: true
  char: w
  byte: 1
  short: 1
  double: 1.0
  float: 3.4
  int: 123
  long: 12
  string: "test"
  objectType:
    enable: true
    mediaFilePath: \resources\media

    media: sea.mp4
    # annotation
    audio: true

```

The following is the toString print result of the object after parsing is completed.

```
{root=RootConfig{ints=[1, 2, 3], map={0=true, 1=false, 2=false}, objectType=ObjectType{enable=true, mediaFilePath='\resources\media', audio=true}, x=true, anInt=123, aByte=1, aChar=w, aShort=1, aLong=12, aDouble=1.0, aFloat=3.4, test='"test"'}}
```


Test Class

```java
package com.os467.testEntity;

import com.os467.annotation.yamlConfig.YamlConfigValue;

import java.util.List;
import java.util.Map;

@YamlConfigValue("root")
public class RootConfig {

    @YamlConfigValue("list")
    private List ints;

    @YamlConfigValue("map")
    private Map<Integer, String> map;

    @YamlConfigValue("objectType")
    private ObjectType objectType;

    @YamlConfigValue("boolean")
    private boolean x;

    @YamlConfigValue("int")
    private Integer anInt;

    @YamlConfigValue("byte")
    private byte aByte;

    @YamlConfigValue("char")
    private Character aChar;

    @YamlConfigValue("short")
    private short aShort;

    @YamlConfigValue("long")
    private long aLong;

    @YamlConfigValue("double")
    private double aDouble;

    @YamlConfigValue("float")
    private float aFloat;

    @YamlConfigValue("string")
    private String test;


    @Override
    public String toString() {
        return "RootConfig{" +
                "ints=" + ints +
                ", map=" + map +
                ", objectType=" + objectType +
                ", x=" + x +
                ", anInt=" + anInt +
                ", aByte=" + aByte +
                ", aChar=" + aChar +
                ", aShort=" + aShort +
                ", aLong=" + aLong +
                ", aDouble=" + aDouble +
                ", aFloat=" + aFloat +
                ", test='" + test + '\'' +
                '}';
    }
}

```

```java
package com.os467.testEntity;

import com.os467.annotation.yamlConfig.YamlConfigValue;
import com.os467.annotation.yamlConfig.YamlConfigValue;

public class ObjectType {

    @YamlConfigValue("enable")
    private Boolean enable;

    @YamlConfigValue("mediaFilePath")
    private String mediaFilePath;

    @YamlConfigValue("audio")
    private Boolean audio;

    @Override
    public String toString() {
        return "ObjectType{" +
                "enable=" + enable +
                ", mediaFilePath='" + mediaFilePath + '\'' +
                ", audio=" + audio +
                '}';
    }
}

```