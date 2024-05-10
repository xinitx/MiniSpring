package org.init.context.event;

import org.init.context.ApplicationContext;

public class ContextStartedEvent extends ApplicationContextEvent {
    public ContextStartedEvent(ApplicationContext source) {
        super(source);
    }
}
