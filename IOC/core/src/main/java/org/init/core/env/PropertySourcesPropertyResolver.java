package org.init.core.env;

import org.init.core.lang.Nullable;

import java.util.Iterator;

public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {
    @Nullable
    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    public boolean containsProperty(String key) {
        if (this.propertySources != null) {
            Iterator var2 = this.propertySources.iterator();

            while (var2.hasNext()) {
                PropertySource<?> propertySource = (PropertySource) var2.next();
                if (propertySource.containsProperty(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public String getProperty(String key) {
        return (String) this.getProperty(key, String.class, true);
    }

    @Nullable
    public <T> T getProperty(String key, Class<T> targetValueType) {
        return this.getProperty(key, targetValueType, true);
    }

    @Nullable
    protected String getPropertyAsRawString(String key) {
        return (String) this.getProperty(key, String.class, false);
    }

    @Nullable
    protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        if (this.propertySources != null) {
            Iterator var4 = this.propertySources.iterator();

            while (var4.hasNext()) {
                PropertySource<?> propertySource = (PropertySource) var4.next();
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Searching for key '" + key + "' in PropertySource '" + propertySource.getName() + "'");
                }

                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (resolveNestedPlaceholders && value instanceof String) {
                        String string = (String) value;
                        value = string;
                    }

                    this.logKeyFound(key, propertySource, value);
                    return (T) value;
                }
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Could not find key '" + key + "' in any property source");
        }

        return null;
    }

    protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() + "' with value of type " + value.getClass().getSimpleName());
        }

    }
}