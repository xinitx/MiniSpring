package org.init.context.event;

public interface ApplicationEventMulticaster {
    void publishEvent(ApplicationEvent event);
    void addApplicationListener(ApplicationListener listener);
    void removeApplicationListener(ApplicationListener<?> var1);

    void removeApplicationListenerBean(String var1);
}
