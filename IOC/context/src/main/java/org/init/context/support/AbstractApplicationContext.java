package org.init.context.support;


import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryPostProcessor;
import org.init.beans.factory.BeanPostProcessor;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.support.BeanDefinitionRegistry;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.ApplicationContext;
import org.init.context.ConfigurableApplicationContext;
import org.init.context.event.*;
import org.init.context.support.ApplicationContextAwareProcessor;
import org.init.core.env.Environment;
import org.init.core.lang.Nullable;
import org.init.test.TestListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {
	private Environment environment;

	@Nullable
	private ApplicationContext parent;
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
	private long startupDate;
	private final AtomicBoolean active = new AtomicBoolean();
	private final AtomicBoolean closed = new AtomicBoolean();
	private ApplicationEventMulticaster applicationEventMulticaster;
	


	@Override
	public Object getBean(String beanName) throws BeansException, ClassNotFoundException {
		return getBeanFactory().getBean(beanName);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

//	public void registerBean(String beanName, Object obj) {
//		getBeanFactory().registerBean(beanName, obj);		
//	}

	@Override
	public boolean isSingleton(String name) throws BeansException {
		return getBeanFactory().isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws BeansException {
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public Class<?> getType(String name) throws BeansException {
		return getBeanFactory().getType(name);
	}
	
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return this.beanFactoryPostProcessors;
	}
	
	
	public void refresh() throws BeansException, IllegalStateException {
		this.prepareRefresh();
		ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
		this.prepareBeanFactory(beanFactory);

		postProcessBeanFactory(beanFactory);
		invokeBeanFactoryPostProcessors(beanFactory);
		registerBeanPostProcessors(beanFactory);
		
		initApplicationEventMulticaster();

		onRefresh();
		
		registerListeners();
		finishBeanFactoryInitialization(beanFactory);
		finishRefresh();
	}

	private void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		try {
			beanFactory.preInstantiateSingletons();
		} catch (BeansException e) {
			throw new RuntimeException(e);
		}
	}

	private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		ArrayList currentRegistryProcessors;
		String[] postProcessorNames;
		Iterator var = this.beanFactoryPostProcessors.iterator();

		while(var.hasNext()) {
			BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)var.next();
			try {
				postProcessor.postProcessBeanFactory(beanFactory);
			} catch (BeansException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		//设置属性

		//必要BeanPostProcessor
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
	}

	private void prepareRefresh() {
	}

	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		try {
			this.refreshBeanFactory();
		} catch (BeansException e) {
			throw new RuntimeException(e);
		}
		ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
		return beanFactory;
	}
	
	protected void registerListeners(){
		ApplicationListener listener = (ApplicationListener) new TestListener();
		this.getApplicationEventMulticaster().addApplicationListener(listener);
	};
	protected void initApplicationEventMulticaster(){
		ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
		this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
		beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);

	};
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory bf){
		//子类去处理
	};
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory bf){
		String[] postProcessorNames;
		try {
			postProcessorNames = bf.getBeanNamesForType(BeanPostProcessor.class);
			for (String postProcessorName : postProcessorNames){
				BeanPostProcessor pp = (BeanPostProcessor) bf.getBean(postProcessorName);
				bf.addBeanPostProcessor(pp);
			}

		} catch (BeansException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	};
	protected void onRefresh(){

	};
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;
	protected void finishRefresh(){
		this.publishEvent(new ContextRefreshEvent("Context Refreshed..."));
	}

	public void publishEvent(ApplicationEvent event) {
		this.getApplicationEventMulticaster().publishEvent(event);
	}

	;

	public void registerSingleton(String beanName, Object singletonObject) {
		getBeanFactory().registerSingleton(beanName, singletonObject);
	}


	public Object getSingleton(String beanName) {
		return getBeanFactory().getSingleton(beanName);
	}


	public boolean containsSingleton(String beanName) {
		return getBeanFactory().containsSingleton(beanName);
	}


	public String[] getSingletonNames() {
		return getBeanFactory().getSingletonNames();
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) throws BeansException {
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ClassNotFoundException {
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		getBeanFactory().addBeanPostProcessor(beanPostProcessor);
		
	}

	@Override
	public int getBeanPostProcessorCount() {
		return getBeanFactory().getBeanPostProcessorCount();
	}

	@Override
	public void registerDependentBean(String beanName, String dependentBeanName) {
		getBeanFactory().registerDependentBean(beanName, dependentBeanName);
	}

	@Override
	public String[] getDependentBeans(String beanName) {
		return getBeanFactory().getDependentBeans(beanName);
	}

	@Override
	public String[] getDependenciesForBean(String beanName) {
		return getBeanFactory().getDependenciesForBean(beanName);
	}

	
	@Override
	public String getApplicationName() {
		return "";
	}
	@Override
	public long getStartupDate() {
		return this.startupDate;
	}
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	@Override
	public Environment getEnvironment() {
		return this.environment;
	}
	
	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		this.beanFactoryPostProcessors.add(postProcessor);
	}


	@Override
	@Nullable
	public ApplicationContext getParent() {
		return parent;
	}

	public void setParent(@Nullable ApplicationContext parent) {
		this.parent = parent;
	}

	@Override
	public void close() {
	}
	
	@Override
	public boolean isActive() {
		return true;
	}

	public ApplicationEventMulticaster getApplicationEventMulticaster() {
		return applicationEventMulticaster;
	}

	public void setApplicationEventPublisher(ApplicationEventMulticaster applicationEventPublisher) {
		this.applicationEventMulticaster = applicationEventPublisher;
	}
}
