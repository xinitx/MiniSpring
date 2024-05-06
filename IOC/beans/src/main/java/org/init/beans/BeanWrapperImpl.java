package org.init.beans;

import org.init.beans.factory.config.PropertyValue;
import org.init.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper {
	Object wrappedObject;
	Class<?> clz;
	public BeanWrapperImpl(Object object) {
		super();	
		this.wrappedObject = object;
		this.clz = object.getClass();
	}

	@Override
	public void setPropertyValue(PropertyValue pv) {
		BeanPropertyHandler propertyHandler = new BeanPropertyHandler(pv.getName());
		PropertyEditor pe = (PropertyEditor) this.getCustomEditor(propertyHandler.getPropertyClz());
		if (pe == null) {
			pe = (PropertyEditor) this.getDefaultEditor(propertyHandler.getPropertyClz());
		}
		if (pe != null) {
			pe.setAsText((String) pv.getValue());
			propertyHandler.setValue(pe.getValue());
		}
		else {
			propertyHandler.setValue(pv.getValue());			
		}
	}
	class BeanPropertyHandler {
		Method writeMethod = null;
		Method readMethod = null;
		Class<?> propertyClz = null;
		
		public Class<?> getPropertyClz() {
			return propertyClz;
		}

		public BeanPropertyHandler(String propertyName) {
			try {
				Field field = clz.getDeclaredField(propertyName);
				propertyClz = field.getType();
				this.writeMethod = clz.getDeclaredMethod("set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1),propertyClz);
				this.readMethod = clz.getDeclaredMethod("get"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} 
		}
		
		public Object getValue() {
			Object result = null;
			writeMethod.setAccessible(true);
			
			try {
				result =  readMethod.invoke(wrappedObject);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return result;

		}

		public void setValue(Object value) {
				writeMethod.setAccessible(true);
				try {
					writeMethod.invoke(wrappedObject, value);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
		}

	}

}
