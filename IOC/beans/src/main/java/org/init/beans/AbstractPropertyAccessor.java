package org.init.beans;


import org.init.beans.factory.config.PropertyValue;

public abstract class AbstractPropertyAccessor extends PropertyEditorRegistrySupport{

	PropertyValues pvs;
	
	public AbstractPropertyAccessor() {
		super();

	}

	
	public void setPropertyValues(PropertyValues pvs) {
		this.pvs = pvs;
		for (PropertyValue pv : this.pvs.getPropertyValues()) {
			setPropertyValue(pv);
		}
	}
	
	public abstract void setPropertyValue(PropertyValue pv) ;

}
