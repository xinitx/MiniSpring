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
    public boolean containsProperty(String name) {
        return this.getProperty(name) != null;
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
    public static PropertySource<?> named(String name) {
        return new ComparisonPropertySource(name);
    }
    static class ComparisonPropertySource extends StubPropertySource {
        private static final String USAGE_ERROR = "ComparisonPropertySource instances are for use with collection comparison only";

        public ComparisonPropertySource(String name) {
            super(name);
        }

        public Object getSource() {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
        }

        public boolean containsProperty(String name) {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
        }

        @Nullable
        public String getProperty(String name) {
            throw new UnsupportedOperationException("ComparisonPropertySource instances are for use with collection comparison only");
        }
    }

    public static class StubPropertySource extends PropertySource<Object> {
        public StubPropertySource(String name) {
            super(name, new Object());
        }

        @Nullable
        public String getProperty(String name) {
            return null;
        }
    }
}
