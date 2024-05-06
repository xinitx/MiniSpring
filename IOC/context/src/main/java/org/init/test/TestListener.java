package org.init.test;

import org.init.context.event.ApplicationEvent;
import org.init.context.event.ApplicationListener;

public class TestListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("listener");
    }
}
