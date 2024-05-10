package org.init.core.env;

import org.init.core.util.ObjectUtils;

public abstract class EnumerablePropertySource<T> extends PropertySource<T> {
    public EnumerablePropertySource(String name, T source) {
        super(name, source);
    }

    protected EnumerablePropertySource(String name) {
        super(name);
    }

    public boolean containsProperty(String name) {
        return ObjectUtils.containsElement(this.getPropertyNames(), name);
    }

    public abstract String[] getPropertyNames();
}
