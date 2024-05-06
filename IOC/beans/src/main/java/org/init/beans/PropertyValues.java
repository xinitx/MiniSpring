package org.init.beans;

import org.init.beans.factory.config.PropertyValue;
import org.init.core.lang.Nullable;

public interface PropertyValues {
    PropertyValue[] getPropertyValues();

    @Nullable
    PropertyValue getPropertyValue(String var1);

    PropertyValues changesSince(PropertyValues var1);

    boolean contains(String var1);

    boolean isEmpty();
}
