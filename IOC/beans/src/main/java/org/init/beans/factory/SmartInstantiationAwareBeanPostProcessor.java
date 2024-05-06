package org.init.beans.factory;

import org.init.beans.BeansException;

import java.lang.reflect.Constructor;

public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor  {

    default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }


    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
