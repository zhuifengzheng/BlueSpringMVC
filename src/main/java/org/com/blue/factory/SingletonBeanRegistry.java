package org.com.blue.factory;

import javax.servlet.ServletException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean注册 单实例
 * @Author by yuanpeng
 * @Date 2020/10/30
 */
public class SingletonBeanRegistry {
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    public SingletonBeanRegistry() {
        singletonObjects.put("singletonBeanRegistry",this);
    }
    /**
     * 注册bean
     * @param beanName
     * @param singletonObject
     * @throws IllegalStateException
     */
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new RuntimeException("bean 实例已经存在 [" + singletonObject +
                        "]  bean 名称是 '" + beanName + "': 已存在的bean是 [" + oldObject + "]");
            }
            this.singletonObjects.put(beanName, singletonObject);
        }
    }


    /**
     * 获取bean
     * @param beanName
     * @return
     */
    public Object getSingleton(String beanName) {
        return this.singletonObjects.get(beanName);
    }


}
