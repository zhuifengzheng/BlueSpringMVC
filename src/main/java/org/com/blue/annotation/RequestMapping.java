package org.com.blue.annotation;

import java.lang.annotation.*;

/**
 * 路径映射注解
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String name() default "";

    String[] value() default {};

    String[] path() default {};

}
