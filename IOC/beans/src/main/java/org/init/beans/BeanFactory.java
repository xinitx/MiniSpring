package org.init.beans;

import org.init.beans.factory.config.ConfigurableListableBeanFactory;

public interface BeanFactory {
    Object getBean(String name) throws BeansException, ClassNotFoundException;
    boolean containsBean(String name);
    //void registerBean(String beanName, Object obj);
    boolean isSingleton(String name) throws BeansException;
    boolean isPrototype(String name) throws BeansException;
    Class<?> getType(String name) throws BeansException;


}
