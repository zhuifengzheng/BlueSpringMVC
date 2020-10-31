package org.com.blue.factory;

/**
 * bean工厂，获取bean
 * @Author by yuanpeng
 * @Date 2020/10/30
 */
public interface BeanFactory {

    Object getBean(String name);
}
