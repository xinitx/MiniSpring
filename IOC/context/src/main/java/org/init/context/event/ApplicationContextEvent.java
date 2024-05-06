package org.init.context.event;

import org.init.context.ApplicationContext;

public abstract class ApplicationContextEvent extends ApplicationEvent {
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext)this.getSource();
    }
}

