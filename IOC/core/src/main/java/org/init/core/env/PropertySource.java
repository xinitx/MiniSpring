package org.init.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;
import org.init.core.util.ObjectUtils;

public abstract class PropertySource<T> {
    protected final Log logger;
    protected final String name;
    protected final T source;
    public PropertySource(String name, T source) {
        this.logger = LogFactory.getLog(this.getClass());
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }
    public PropertySource(String name) {
        this(name, (T) new Object());
    }
    public Log getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public T getSource() {
        return source;
    }
    @Nullable
    public abstract Object getProperty(String var1);

    public boolean equals(Object obj) {
        return this == obj || obj instanceof PropertySource && ObjectUtils.nullSafeEquals(this.name, ((PropertySource)obj).name);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.name);
    }

    public String toString() {
        return this.logger.isDebugEnabled() ? this.getClass().getSimpleName() + "@" + System.identityHashCode(this) + " {name='" + this.name + "', properties=" + this.source + "}" : this.getClass().getSimpleName() + " {name='" + this.name + "'}";
    }
}
