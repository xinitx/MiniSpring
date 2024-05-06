package org.init.beans.factory.config;


import org.init.beans.BeanFactory;
import org.init.beans.BeansException;

public interface AutowireCapableBeanFactory extends BeanFactory {
	int AUTOWIRE_NO = 0;
	int AUTOWIRE_BY_NAME = 1;
	int AUTOWIRE_BY_TYPE = 2;

	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

}
