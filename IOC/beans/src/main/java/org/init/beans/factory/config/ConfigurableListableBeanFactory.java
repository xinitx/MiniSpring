package org.init.beans.factory.config;


import org.init.beans.BeansException;

public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {
	void preInstantiateSingletons() throws BeansException;
}
