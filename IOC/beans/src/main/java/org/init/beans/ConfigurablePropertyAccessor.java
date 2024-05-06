package org.init.beans;



public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {
    void setPropertyValues(PropertyValues mpvs);
}
