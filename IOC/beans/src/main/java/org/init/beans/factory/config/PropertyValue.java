package org.init.beans.factory.config;

import org.init.core.lang.Nullable;
import org.init.core.util.Assert;

public class PropertyValue {
	private final String type;
	private final String name;
	private final Object value;
	private final boolean isRef;
	public PropertyValue(PropertyValue original, @Nullable Object value) {
		this.name = original.getName();
		this.value = value;
		this.type = original.getType();
		this.isRef = original.isRef;
	}
	public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
		this.isRef = false;
		this.type = value.getClass().getName();
	}

	public PropertyValue(String type, String name, Object value, boolean isRef) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.isRef = isRef;
	}
	public PropertyValue(PropertyValue original) {
		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = original.getValue();
		this.type = original.getType();
		this.isRef = original.isRef;
	}

	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}
	public boolean getIsRef() {
		return isRef;
	}

}

