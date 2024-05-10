package org.init.beans;

import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.core.lang.Nullable;

public interface BeanFactory {
    Object getBean(String name) throws BeansException;
    boolean containsBean(String name);
    //void registerBean(String beanName, Object obj);
    boolean isSingleton(String name) throws BeansException;
    boolean isPrototype(String name) throws BeansException;
    Class<?> getType(String name) throws BeansException;

    boolean isTypeMatch(String var1, @Nullable Class<?> var2);
}
