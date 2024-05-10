package org.init.context;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.event.ApplicationEvent;
import org.init.context.event.ApplicationListener;
import org.init.context.event.ContextStartedEvent;
import org.init.context.support.AbstractApplicationContext;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.env.Environment;
import org.init.core.io.ClassPathResource;
import org.init.core.io.Resource;
import org.init.core.util.ClassUtils;

import java.io.IOException;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private Boolean allowBeanDefinitionOverriding;
    private LifecycleProcessor lifecycleProcessor;
    private Boolean allowCircularReferences;
    private Resource[] configResources;
    private String configLocation;
    private volatile DefaultListableBeanFactory beanFactory;
    public ClassPathXmlApplicationContext(String fileName){
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh){
        this.configLocation = fileName;
        if (isRefresh) {
            try {
                refresh();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }
    protected final boolean hasBeanFactory() {
        return this.beanFactory != null;
    }
    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {
        if (this.hasBeanFactory()) {
            this.destroyBeans();
            this.closeBeanFactory();
        }

        try {
            DefaultListableBeanFactory beanFactory = this.createBeanFactory();
            this.customizeBeanFactory(beanFactory);
            this.loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        } catch (IOException var2) {
            throw new RuntimeException("I/O error parsing bean definition source");
        }
    }
    protected void destroyBeans() {
        this.getBeanFactory().destroySingletons();
    }
    protected final void closeBeanFactory() {
        DefaultListableBeanFactory beanFactory = this.beanFactory;
        if (beanFactory != null) {
            this.beanFactory = null;
        }
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }



    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(this.getInternalParentBeanFactory());
    }
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }

        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }

    }

    public boolean isCurrentlyInCreation(String var1) {
        return this.isCurrentlyInCreation(var1);
    }


    public ClassLoader getBeanClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }


    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions(this.configLocation);
    }
    @Override
    public boolean isRunning() {
        return this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning();
    }
    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
        } else {
            return this.lifecycleProcessor;
        }
    }
    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }
}
