package org.com.blue.annotation;

import java.lang.annotation.*;

/**
 * 返回参数序列化注解标识
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
