package org.init.context.event;

import org.init.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.List;

public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster{
    List<ApplicationListener> listeners = new ArrayList<>();
    ConfigurableListableBeanFactory beanFactory;
    public SimpleApplicationEventMulticaster(){}
    public SimpleApplicationEventMulticaster(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    @Override
    public void publishEvent(ApplicationEvent event) {
        for (ApplicationListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> var1) {
        this.listeners.remove(var1);
    }

    @Override
    public void removeApplicationListenerBean(String var1) {
        this.listeners.remove(var1);
    }
}
