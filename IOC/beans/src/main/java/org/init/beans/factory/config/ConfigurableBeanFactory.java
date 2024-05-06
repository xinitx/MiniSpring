package org.init.beans.factory.config;


import org.init.beans.BeanFactory;
import org.init.beans.factory.BeanPostProcessor;

public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {

	String SCOPE_SINGLETON = "singleton";
	String SCOPE_PROTOTYPE = "prototype";
	boolean isCurrentlyInCreation(String var1);
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	int getBeanPostProcessorCount();

	void registerDependentBean(String beanName, String dependentBeanName);

	String[] getDependentBeans(String beanName);

	String[] getDependenciesForBean(String beanName);
	ClassLoader getBeanClassLoader();

}

