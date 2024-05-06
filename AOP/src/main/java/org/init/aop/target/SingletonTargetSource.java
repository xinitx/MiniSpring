package org.init.aop.target;

import org.init.aop.framework.TargetSource;

import java.io.Serializable;

public class SingletonTargetSource implements TargetSource, Serializable {
    private final Object target;
    public SingletonTargetSource(Object target) {
        this.target = target;
    }

    public Class<?> getTargetClass() {
        return this.target.getClass();
    }

    public Object getTarget() {
        return this.target;
    }

    public boolean isStatic() {
        return true;
    }



    public void releaseTarget(Object target) {
    }
}
