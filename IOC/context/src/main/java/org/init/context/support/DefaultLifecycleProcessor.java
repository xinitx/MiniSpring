package org.init.context.support;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryAware;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.context.LifecycleProcessor;

public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {
    private volatile long timeoutPerShutdownPhase = 30000L;
    private volatile boolean running;
    private volatile ConfigurableListableBeanFactory beanFactory;
    public DefaultLifecycleProcessor() {
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        } else {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClose() {

    }
}
