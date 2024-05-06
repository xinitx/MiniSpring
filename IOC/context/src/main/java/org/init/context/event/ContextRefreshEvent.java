package org.init.context.event;

public class ContextRefreshEvent extends ApplicationEvent {

    public ContextRefreshEvent(Object arg0) {
        super(arg0);
    }
    public String toString() {
        return this.msg;
    }
}