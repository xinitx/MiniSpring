package org.init.beans.factory;


import org.init.beans.BeanFactory;
import org.init.beans.BeansException;

public interface BeanPostProcessor {
	Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

	Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
