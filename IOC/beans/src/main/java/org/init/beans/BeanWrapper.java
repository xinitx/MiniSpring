package org.init.beans;

import org.init.beans.factory.config.PropertyValue;

public interface BeanWrapper  extends ConfigurablePropertyAccessor{
    void setPropertyValue(PropertyValue pv) throws BeansException;
}
