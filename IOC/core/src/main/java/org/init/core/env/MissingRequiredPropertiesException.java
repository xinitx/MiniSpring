package org.init.core.env;

import java.util.LinkedHashSet;
import java.util.Set;

public class MissingRequiredPropertiesException extends IllegalStateException {
    private final Set<String> missingRequiredProperties = new LinkedHashSet();

    public MissingRequiredPropertiesException() {
    }

    void addMissingRequiredProperty(String key) {
        this.missingRequiredProperties.add(key);
    }

    public String getMessage() {
        return "The following properties were declared as required but could not be resolved: " + this.getMissingRequiredProperties();
    }

    public Set<String> getMissingRequiredProperties() {
        return this.missingRequiredProperties;
    }
}
