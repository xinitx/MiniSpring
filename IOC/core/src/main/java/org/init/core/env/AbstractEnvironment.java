package org.init.core.env;

import org.init.core.SpringProperties;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;
import org.init.core.util.ObjectUtils;
import org.init.core.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractEnvironment implements ConfigurableEnvironment  {
    private final MutablePropertySources propertySources;
    private final ConfigurablePropertyResolver propertyResolver;
    private final Set<String> activeProfiles;
    private final Set<String> defaultProfiles;
    public AbstractEnvironment() {
        this(new MutablePropertySources());

    }
    protected AbstractEnvironment(MutablePropertySources propertySources) {
        this.propertySources = propertySources;
        this.propertyResolver = this.createPropertyResolver(propertySources);
        this.activeProfiles = new LinkedHashSet();
        this.defaultProfiles = new LinkedHashSet(this.getReservedDefaultProfiles());
        this.customizePropertySources(propertySources);
    }
    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new PropertySourcesPropertyResolver(propertySources);
    }

    protected final ConfigurablePropertyResolver getPropertyResolver() {
        return this.propertyResolver;
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    protected Set<String> getReservedDefaultProfiles() {
        return Collections.singleton("default");
    }

    public String[] getActiveProfiles() {
        return StringUtils.toStringArray(this.doGetActiveProfiles());
    }

    protected Set<String> doGetActiveProfiles() {
        synchronized(this.activeProfiles) {
            if (this.activeProfiles.isEmpty()) {
                String profiles = this.doGetActiveProfilesProperty();
                if (StringUtils.hasText(profiles)) {
                    this.setActiveProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }

            return this.activeProfiles;
        }
    }

    @Nullable
    protected String doGetActiveProfilesProperty() {
        return this.getProperty("spring.profiles.active");
    }

    public void setActiveProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");


        synchronized(this.activeProfiles) {
            this.activeProfiles.clear();
            String[] var3 = profiles;
            int var4 = profiles.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String profile = var3[var5];
                this.validateProfile(profile);
                this.activeProfiles.add(profile);
            }

        }
    }

    public void addActiveProfile(String profile) {


        this.validateProfile(profile);
        this.doGetActiveProfiles();
        synchronized(this.activeProfiles) {
            this.activeProfiles.add(profile);
        }
    }

    public String[] getDefaultProfiles() {
        return StringUtils.toStringArray(this.doGetDefaultProfiles());
    }

    protected Set<String> doGetDefaultProfiles() {
        synchronized(this.defaultProfiles) {
            if (this.defaultProfiles.equals(this.getReservedDefaultProfiles())) {
                String profiles = this.doGetDefaultProfilesProperty();
                if (StringUtils.hasText(profiles)) {
                    this.setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }

            return this.defaultProfiles;
        }
    }

    @Nullable
    protected String doGetDefaultProfilesProperty() {
        return this.getProperty("spring.profiles.default");
    }

    public void setDefaultProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        synchronized(this.defaultProfiles) {
            this.defaultProfiles.clear();
            String[] var3 = profiles;
            int var4 = profiles.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String profile = var3[var5];
                this.validateProfile(profile);
                this.defaultProfiles.add(profile);
            }

        }
    }

    /** @deprecated */
    @Deprecated
    public boolean acceptsProfiles(String... profiles) {

        String[] var2 = profiles;
        int var3 = profiles.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String profile = var2[var4];
            if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
                if (!this.isProfileActive(profile.substring(1))) {
                    return true;
                }
            } else if (this.isProfileActive(profile)) {
                return true;
            }
        }

        return false;
    }



    protected boolean isProfileActive(String profile) {
        this.validateProfile(profile);
        Set<String> currentActiveProfiles = this.doGetActiveProfiles();
        return currentActiveProfiles.contains(profile) || currentActiveProfiles.isEmpty() && this.doGetDefaultProfiles().contains(profile);
    }

    protected void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
        } else if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
        }
    }

    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    public Map<String, Object> getSystemProperties() {
        return (Map)System.getProperties();
    }

    public Map<String, Object> getSystemEnvironment() {
        return this.suppressGetenvAccess() ? Collections.emptyMap() : (Map)System.getenv();
    }

    protected boolean suppressGetenvAccess() {
        return SpringProperties.getFlag("spring.getenv.ignore");
    }

    public void merge(ConfigurableEnvironment parent) {
        Iterator var2 = parent.getPropertySources().iterator();

        while(var2.hasNext()) {
            PropertySource<?> ps = (PropertySource)var2.next();
            if (!this.propertySources.contains(ps.getName())) {
                this.propertySources.addLast(ps);
            }
        }

        String[] parentActiveProfiles = parent.getActiveProfiles();
        if (!ObjectUtils.isEmpty(parentActiveProfiles)) {
            synchronized(this.activeProfiles) {
                Collections.addAll(this.activeProfiles, parentActiveProfiles);
            }
        }

        String[] parentDefaultProfiles = parent.getDefaultProfiles();
        if (!ObjectUtils.isEmpty(parentDefaultProfiles)) {
            synchronized(this.defaultProfiles) {
                this.defaultProfiles.remove("default");
                Collections.addAll(this.defaultProfiles, parentDefaultProfiles);
            }
        }

    }



    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
    }

    public void setValueSeparator(@Nullable String valueSeparator) {
        this.propertyResolver.setValueSeparator(valueSeparator);
    }

    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
    }

    public void setRequiredProperties(String... requiredProperties) {
        this.propertyResolver.setRequiredProperties(requiredProperties);
    }

    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
        this.propertyResolver.validateRequiredProperties();
    }

    public boolean containsProperty(String key) {
        return this.propertyResolver.containsProperty(key);
    }

    @Nullable
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Nullable
    public <T> T getProperty(String key, Class<T> targetType) {
        return this.propertyResolver.getProperty(key, targetType);
    }

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return this.propertyResolver.getProperty(key, targetType, defaultValue);
    }

    public String getRequiredProperty(String key) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key);
    }

    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key, targetType);
    }





    public String toString() {
        String var10000 = this.getClass().getSimpleName();
        return var10000 + " {activeProfiles=" + this.activeProfiles + ", defaultProfiles=" + this.defaultProfiles + ", propertySources=" + this.propertySources + "}";
    }

}
