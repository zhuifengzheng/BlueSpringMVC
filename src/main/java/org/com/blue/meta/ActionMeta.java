package org.com.blue.meta;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 请求元数据封装：
 * 存储@RquestMapping路径对应的方法
 * 用来请求过来路由到指定到方法
 *
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
@Data
public class ActionMeta implements Serializable {

    private static final long serialVersionUID = 856937636942888190L;

    private Class<?> clazz;

    private Method method;

}
