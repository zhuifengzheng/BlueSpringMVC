package org.com.blue.annotation;

import java.lang.annotation.*;

/**
 * controller层标记注解
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}
