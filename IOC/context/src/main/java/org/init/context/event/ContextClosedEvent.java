package org.init.context.event;

import org.init.context.ApplicationContext;

public class ContextClosedEvent extends ApplicationContextEvent {
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}
