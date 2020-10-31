package org.com.blue.context;

import org.com.blue.meta.ActionMeta;
import org.com.blue.meta.BeanDefinitionMeta;
import org.com.blue.meta.RequestContext;
import org.com.blue.utils.ActionContextUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求上下文
 *
 * @Author by yuanpeng
 * @Date 2020/10/28
 */
public class ActionContext {
    public static List<Class<?>> classList;

    public static Map<String, ActionMeta> methodMap;

    private static ThreadLocal<RequestContext> contextThreadLocal = new ThreadLocal<>();

    static {
        methodMap = ActionContextUtil.loadActionMeta();
        classList = ActionContextUtil.loadBeanDefinations();
    }

    public ActionContext() {

    }

    public static RequestContext getRequestContext(){
        return contextThreadLocal.get();
    }

    public static void setRequestContext(RequestContext requestContext){
        contextThreadLocal.set(requestContext);
    }

    public static void removeRequestContext(){
        contextThreadLocal.remove();
    }
}
