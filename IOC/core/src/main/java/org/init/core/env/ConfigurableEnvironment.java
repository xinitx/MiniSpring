package org.init.core.env;

import java.util.List;
import java.util.Map;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {
    void setActiveProfiles(String... var1);

    void addActiveProfile(String var1);

    void setDefaultProfiles(String... var1);

    List<PropertySource<?>> getPropertySources();

    Map<String, Object> getSystemEnvironment();

    Map<String, Object> getSystemProperties();

    void merge(ConfigurableEnvironment var1);
}