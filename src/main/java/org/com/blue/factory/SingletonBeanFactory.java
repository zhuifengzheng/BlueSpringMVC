package org.com.blue.factory;

/**
 * @Author by yuanpeng
 * @Date 2020/10/30
 */
public class SingletonBeanFactory extends SingletonBeanRegistry implements BeanFactory{
    public SingletonBeanFactory(){

    }
    @Override
    public Object getBean(String name) {
        return getSingleton(name);
    }
}
