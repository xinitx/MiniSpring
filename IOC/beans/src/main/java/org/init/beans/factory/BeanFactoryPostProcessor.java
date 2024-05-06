package org.init.beans.factory;


import org.init.beans.BeanFactory;
import org.init.beans.BeansException;

public interface BeanFactoryPostProcessor {
	void postProcessBeanFactory(BeanFactory beanFactory) throws BeansException;
}
