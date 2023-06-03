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
{root=RootConfig{ints=[1, 2, 3], map={0=true, 1=false, 2=false}, objectType=com.os467.testEntity.ObjectType@61443d8f, x=true, anInt=123, aByte=1, aChar=w, aShort=1, aLong=12, aDouble=1.0, aFloat=3.4, test='"测试数据"'}}
```