package com.os467.annotation.yamlConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 当这个注解被标记在一个类上时，它意味着被这个注解标记的类是根配置类。
 * 它应该在指定的配置文件中拥有一个根配置项、否则，如果根配置项不存在，将抛出一个缺失配置项的异常。
 * 如果根配置项不存在，将抛出一个缺失配置项的异常。
 * 当此注解被标记在根配置类属性上时、意味着由该注释标记的属性在指定配置的根配置下有一个子配置
 * 在指定的配置文件的根配置之下。
 * 如果没有子配置存在，那么将抛出一个缺少配置项的异常。
 * 如果注解只被标记在一个类属性上、那么这个属性也将被注入配置值，当作为根配置类的子配置属性被注入。
 * 如果注解只被标记在类上、它将对该类没有影响，但相应的根配置必须存在于配置文件中。
 *
 *
 * When this annotation is marked on a class,it means that
 * the class marked by this annotation is the root configuration class.
 * It should have a root configuration item in the specified configuration file,
 * or a missing configuration item exception will be thrown
 * if the root configuration item does not exist.
 *
 * When this annotation is marked on the root configuration class attribute,
 * it means that the attribute marked by this annotation has a sub-configuration
 * under the root configuration of the specified configuration file.
 * If no sub-configuration exists then a missing configuration item exception will be thrown.
 *
 * If the annotation is marked on a class property only,
 * then this property will also be injected with configuration values when
 * injected as a sub-configuration property of a root configuration class.
 * If the annotation is only marked on the class,
 * it will have no effect on the class, but the corresponding root configuration
 * must be present in the configuration file.
 *
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface YamlConfigValue {
    String value() default "";
}
