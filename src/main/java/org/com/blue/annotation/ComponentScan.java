package org.com.blue.annotation;

import java.lang.annotation.*;

/**
 * 包扫描注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    /**
     * 扫描包路径
     * @return
     */
    String[] value() default {};
}
