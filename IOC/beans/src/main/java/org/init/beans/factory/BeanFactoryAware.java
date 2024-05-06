package org.init.beans.factory;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;

public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
