package org.init.context;

import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryPostProcessor;
import org.init.beans.factory.config.ConfigurableBeanFactory;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.config.ListableBeanFactory;
import org.init.context.event.ApplicationEventPublisher;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.env.Environment;
import org.init.core.env.EnvironmentCapable;
import org.init.core.lang.Nullable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory, ApplicationEventPublisher {
    String getApplicationName();
    long getStartupDate();
    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
    void setEnvironment(ConfigurableEnvironment var1);
    ConfigurableEnvironment getEnvironment();
    void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);
    void refresh() throws BeansException, IllegalStateException;
    void close();
    boolean isActive();
    @Nullable
    ApplicationContext getParent();
}
