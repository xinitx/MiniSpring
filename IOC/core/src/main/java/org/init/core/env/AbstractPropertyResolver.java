package org.init.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private boolean ignoreUnresolvableNestedPlaceholders = false;
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";
    @Nullable
    private String valueSeparator = ":";
    private final Set<String> requiredProperties = new LinkedHashSet();

    public AbstractPropertyResolver() {
    }





    public void setPlaceholderPrefix(String placeholderPrefix) {
        Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
        this.placeholderPrefix = placeholderPrefix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
        this.placeholderSuffix = placeholderSuffix;
    }

    public void setValueSeparator(@Nullable String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
    }

    public void setRequiredProperties(String... requiredProperties) {
        Collections.addAll(this.requiredProperties, requiredProperties);
    }

    public void validateRequiredProperties() {
        MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
        Iterator var2 = this.requiredProperties.iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            if (this.getProperty(key) == null) {
                ex.addMissingRequiredProperty(key);
            }
        }

        if (!ex.getMissingRequiredProperties().isEmpty()) {
            throw ex;
        }
    }

    public boolean containsProperty(String key) {
        return this.getProperty(key) != null;
    }

    @Nullable
    public String getProperty(String key) {
        return (String)this.getProperty(key, String.class);
    }

    public String getProperty(String key, String defaultValue) {
        String value = this.getProperty(key);
        return value != null ? value : defaultValue;
    }

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        T value = this.getProperty(key, targetType);
        return value != null ? value : defaultValue;
    }

    public String getRequiredProperty(String key) throws IllegalStateException {
        String value = this.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Required key '" + key + "' not found");
        } else {
            return value;
        }
    }

    public <T> T getRequiredProperty(String key, Class<T> valueType) throws IllegalStateException {
        T value = this.getProperty(key, valueType);
        if (value == null) {
            throw new IllegalStateException("Required key '" + key + "' not found");
        } else {
            return value;
        }
    }











    @Nullable
    protected abstract String getPropertyAsRawString(String key);
}

