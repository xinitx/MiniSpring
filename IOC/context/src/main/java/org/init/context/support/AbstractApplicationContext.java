package org.init.context.support;


import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryPostProcessor;
import org.init.beans.factory.BeanPostProcessor;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.support.BeanDefinitionRegistry;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.ApplicationContext;
import org.init.context.ConfigurableApplicationContext;
import org.init.context.LifecycleProcessor;
import org.init.context.event.*;
import org.init.context.support.ApplicationContextAwareProcessor;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.env.Environment;
import org.init.core.env.StandardEnvironment;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;
import org.init.core.util.ObjectUtils;
import org.init.core.util.ReflectionUtils;
import org.init.test.TestListener;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {
	private ConfigurableEnvironment environment;
	private Set<ApplicationListener<?>> earlyApplicationListeners;
	private String id;
	private String displayName;
	private final Set<ApplicationListener<?>> applicationListeners;
	private LifecycleProcessor lifecycleProcessor;
	@Nullable
	private ApplicationContext parent;
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
	private long startupDate;
	private Thread shutdownHook;
	private final Object startupShutdownMonitor;
	private final AtomicBoolean active = new AtomicBoolean();
	private final AtomicBoolean closed = new AtomicBoolean();
	private ApplicationEventMulticaster applicationEventMulticaster;
	private Set<ApplicationEvent> earlyApplicationEvents;
	public AbstractApplicationContext(){
		this.id = ObjectUtils.identityToString(this);
		this.applicationListeners = new LinkedHashSet();
		this.startupShutdownMonitor = new Object();
	}

	public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		this();
		this.setParent(parent);
	}

	@Override
	public Object getBean(String beanName) throws BeansException {
		return getBeanFactory().getBean(beanName);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

//	public void registerBean(String beanName, Object obj) {
//		getBeanFactory().registerBean(beanName, obj);		
//	}
	public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) {
		this.assertBeanFactoryActive();
		return this.getBeanFactory().isTypeMatch(name, typeToMatch);
	}
	protected void assertBeanFactoryActive() {
		if (!this.active.get()) {
			if (this.closed.get()) {
				throw new IllegalStateException(this.getDisplayName() + " has been closed already");
			} else {
				throw new IllegalStateException(this.getDisplayName() + " has not been refreshed yet");
			}
		}
	}
	public String getDisplayName() {
		return this.displayName;
	}
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
		synchronized(this.startupShutdownMonitor) {
			//1. 初始化startupDate(System.currentTimeMillis())、closed(false)、active(true)、earlyApplicationEvents属性
			//2. this.initPropertySources()空方法提供拓展，主要针对Environment对象，比如XmlWebApplicationContext父类AbstractRefreshableWebApplicationContext在这个方法给Environment对象添加了servletContext、servletConfig
			//3. this.getEnvironment().validateRequiredProperties()方法验证环境，在this.initPropertySources()方法可以getEnvironment().setRequiredProperties("MYSQL_HOST"); 从而自定义环境变量验证，失败抛出异常
			this.prepareRefresh();

			ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
			this.prepareBeanFactory(beanFactory);
			try {
				this.postProcessBeanFactory(beanFactory);
				this.invokeBeanFactoryPostProcessors(beanFactory);
				this.registerBeanPostProcessors(beanFactory);
				//消息处理，暂时为空
				this.initMessageSource();
				this.initApplicationEventMulticaster();
				this.onRefresh();
				this.registerListeners();
				this.finishBeanFactoryInitialization(beanFactory);
				this.finishRefresh();
			}catch (BeansException var) {
				System.out.println("Exception encountered during context initialization");
				this.destroyBeans();
				this.cancelRefresh(var);
				throw var;
			} finally {
				this.resetCommonCaches();
			}

		}

	}

	private void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		try {
			beanFactory.preInstantiateSingletons();
		} catch (BeansException e) {
			throw new RuntimeException(e);
		}
	}

	private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		Iterator var1 = this.beanFactoryPostProcessors.iterator();
		while(var1.hasNext()) {
			BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)var1.next();
			try {
				postProcessor.postProcessBeanFactory(beanFactory);
			} catch (BeansException e) {
				throw new RuntimeException(e);
			}
		}
		String[] var2 = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class);
		for (String beanName : var2){
			if (beanFactory.isTypeMatch(beanName, BeanFactoryPostProcessor.class)) {
				try {
					BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)beanFactory.getBean(beanName);
					postProcessor.postProcessBeanFactory(beanFactory);
				} catch (BeansException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		//设置属性

		//必要BeanPostProcessor
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
		if (!beanFactory.containsLocalBean("environment")) {
			beanFactory.registerSingleton("environment", this.getEnvironment());
		}

		if (!beanFactory.containsLocalBean("systemProperties")) {
			beanFactory.registerSingleton("systemProperties", this.getEnvironment().getSystemProperties());
		}

		if (!beanFactory.containsLocalBean("systemEnvironment")) {
			beanFactory.registerSingleton("systemEnvironment", this.getEnvironment().getSystemEnvironment());
		}
	}

	private void prepareRefresh() {
		this.startupDate = System.currentTimeMillis();
		this.closed.set(false);
		this.active.set(true);
		this.initPropertySources();
		this.getEnvironment().validateRequiredProperties();
		this.earlyApplicationEvents = new LinkedHashSet();
	}
	protected void initPropertySources() {
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
		try {
			String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class);
			for (String listenerBeanName : listenerBeanNames){
				ApplicationListener listener = (ApplicationListener) this.getBean(listenerBeanName);
				this.getApplicationEventMulticaster().addApplicationListener(listener);
			}
		} catch (BeansException e) {
			throw new RuntimeException(e);
		}
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
		}
	};

	protected void onRefresh() throws BeansException{

	};

	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

	protected void finishRefresh(){
		//this.clearResourceCaches();
		this.initLifecycleProcessor();
		this.getLifecycleProcessor().onRefresh();
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
	public String[] getBeanNamesForType(Class<?> type) {
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ClassNotFoundException {
		return getBeanFactory().getBeansOfType(type);
	}


	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		getBeanFactory().addBeanPostProcessor(beanPostProcessor);

	}


	public int getBeanPostProcessorCount() {
		return getBeanFactory().getBeanPostProcessorCount();
	}


	public void registerDependentBean(String beanName, String dependentBeanName) {
		getBeanFactory().registerDependentBean(beanName, dependentBeanName);
	}


	public String[] getDependentBeans(String beanName) {
		return getBeanFactory().getDependentBeans(beanName);
	}


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
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = this.createEnvironment();
		}
		return this.environment;
	}
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardEnvironment();
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

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	public ApplicationEventMulticaster getApplicationEventMulticaster() {
		return applicationEventMulticaster;
	}

	@Nullable
	protected BeanFactory getInternalParentBeanFactory() {
		ApplicationContext p = this.getParent();
		Object var;
		if (p instanceof ConfigurableApplicationContext ) {
			var = p.getBeanFactory();
		} else {
			var = this.getParent();
		}

		return (BeanFactory)var;
	}

	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
		if (this.applicationEventMulticaster != null) {
			this.applicationEventMulticaster.addApplicationListener(listener);
		}

		this.applicationListeners.add(listener);
	}

	public BeanFactory getParentBeanFactory() {
		return this.getParent();
	}

	public boolean containsLocalBean(String name) {
		return this.getBeanFactory().containsLocalBean(name);
	}

	public void setApplicationEventPublisher(ApplicationEventMulticaster applicationEventPublisher) {
		this.applicationEventMulticaster = applicationEventPublisher;
	}

	public void registerShutdownHook() {
		if (this.shutdownHook == null) {
			this.shutdownHook = new Thread("SpringContextShutdownHook") {
				public void run() {
					synchronized(AbstractApplicationContext.this.startupShutdownMonitor) {
						AbstractApplicationContext.this.doClose();
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		}

	}

	protected void doClose() {
		if (this.active.get() && this.closed.compareAndSet(false, true)) {
			System.out.println("SpringContext Shutdown...");
			this.publishEvent((ApplicationEvent)(new ContextClosedEvent(this)));
			this.destroyBeans();
			this.closeBeanFactory();
			this.onClose();
			if (this.earlyApplicationListeners != null) {
				this.applicationListeners.clear();
				this.applicationListeners.addAll(this.earlyApplicationListeners);
			}

			this.active.set(false);
		}
	}

	protected void onClose() {
	}

	protected abstract void closeBeanFactory();

	protected void destroyBeans() {
		this.getBeanFactory().destroySingletons();
	}
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
		if (beanFactory.containsLocalBean("lifecycleProcessor")) {
			try {
				this.lifecycleProcessor = (LifecycleProcessor)beanFactory.getBean("lifecycleProcessor");
			} catch (BeansException e) {
				throw new RuntimeException(e);
			}
		} else {
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			this.lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton("lifecycleProcessor", this.lifecycleProcessor);
		}
	}
	public void start() {
		this.getLifecycleProcessor().start();
		this.publishEvent((ApplicationEvent)(new ContextStartedEvent(this)));
	}

	public void stop() {
		this.getLifecycleProcessor().stop();
		this.publishEvent((ApplicationEvent)(new ContextStoppedEvent(this)));
	}
	LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
		if (this.lifecycleProcessor == null) {
			throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
		} else {
			return this.lifecycleProcessor;
		}
	}

	protected void cancelRefresh(BeansException ex) {
		this.active.set(false);
	}
	protected void resetCommonCaches() {
		ReflectionUtils.clearCache();
	}
	protected void initMessageSource() {
		//暂无
	}

}
