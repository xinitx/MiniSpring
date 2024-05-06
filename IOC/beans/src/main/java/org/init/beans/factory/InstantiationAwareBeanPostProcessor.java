package org.init.beans.factory;

import org.init.beans.BeansException;
import org.init.beans.factory.config.PropertyValue;

import java.beans.PropertyDescriptor;
import java.util.List;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor  {

    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }
    default List<PropertyValue> postProcessPropertyValues(List<PropertyValue> pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }
}
