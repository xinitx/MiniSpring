package org.init.beans.factory.config;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {

	boolean containsBeanDefinition(String beanName);

	int getBeanDefinitionCount();

	String[] getBeanDefinitionNames();

	String[] getBeanNamesForType(Class<?> type);

	<T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ClassNotFoundException;

}

