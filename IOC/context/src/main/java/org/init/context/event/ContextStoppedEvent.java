package org.init.context.event;

import org.init.context.ApplicationContext;

public class ContextStoppedEvent extends ApplicationContextEvent {
    public ContextStoppedEvent(ApplicationContext source) {
        super(source);
    }
}

