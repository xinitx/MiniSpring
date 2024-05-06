package org.init.core.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractEnvironment implements ConfigurableEnvironment  {
    private final List<PropertySource> propertySources;
    private final ConfigurablePropertyResolver propertyResolver;
    public AbstractEnvironment() {
        this.propertySources = new CopyOnWriteArrayList<PropertySource>();
        this.propertyResolver = null;
    }

    @Override
    public void setActiveProfiles(String... var1) {

    }

    @Override
    public void addActiveProfile(String var1) {

    }

    @Override
    public void setDefaultProfiles(String... var1) {

    }

    @Override
    public List<PropertySource<?>> getPropertySources() {
        return null;
    }

    @Override
    public Map<String, Object> getSystemEnvironment() {
        return null;
    }

    @Override
    public Map<String, Object> getSystemProperties() {
        return null;
    }

    @Override
    public void merge(ConfigurableEnvironment var1) {

    }

    @Override
    public void setPlaceholderPrefix(String var1) {

    }

    @Override
    public void setPlaceholderSuffix(String var1) {

    }

    @Override
    public void setValueSeparator(String var1) {

    }

    @Override
    public void setIgnoreUnresolvableNestedPlaceholders(boolean var1) {

    }

    @Override
    public void setRequiredProperties(String... var1) {

    }

    @Override
    public void validateRequiredProperties() throws RuntimeException {

    }

    @Override
    public String[] getActiveProfiles() {
        return new String[0];
    }

    @Override
    public String[] getDefaultProfiles() {
        return new String[0];
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        return false;
    }

    @Override
    public boolean containsProperty(String key) {
        return false;
    }

    @Override
    public String getProperty(String key) {
        return null;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return null;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return null;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return null;
    }

    @Override
    public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType) {
        return null;
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return null;
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return null;
    }

    @Override
    public String resolvePlaceholders(String text) {
        return null;
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return null;
    }
}
