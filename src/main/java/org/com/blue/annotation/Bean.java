package org.com.blue.annotation;

import java.lang.annotation.*;

/**
 * domain层标记注解
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
}
