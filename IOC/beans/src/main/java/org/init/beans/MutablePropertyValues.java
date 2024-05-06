package org.init.beans;

import org.init.beans.factory.config.PropertyValue;
import org.init.core.lang.Nullable;

import java.io.Serializable;
import java.util.*;

public class MutablePropertyValues implements PropertyValues, Serializable {
    private final List<PropertyValue> propertyValueList;
    @Nullable
    private Set<String> processedProperties;
    private volatile boolean converted = false;
    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList(0);
    }
    public MutablePropertyValues(@Nullable PropertyValues original) {
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList(pvs.length);
            PropertyValue[] var = pvs;

            for(PropertyValue pv : var) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        } else {
            this.propertyValueList = new ArrayList(0);
        }
    }
    public MutablePropertyValues(@Nullable Map<?, ?> original) {
        if (original != null) {
            this.propertyValueList = new ArrayList(original.size());
            original.forEach((attrName, attrValue) -> {
                this.propertyValueList.add(new PropertyValue(attrName.toString(), attrValue));
            });
        } else {
            this.propertyValueList = new ArrayList(0);
        }
    }
    @Override
    public PropertyValue[] getPropertyValues() {
        return (PropertyValue[])this.propertyValueList.toArray(new PropertyValue[0]);
    }

    @Nullable
    public PropertyValue getPropertyValue(String propertyName) {
        Iterator var2 = this.propertyValueList.iterator();

        PropertyValue pv;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            pv = (PropertyValue)var2.next();
        } while(!pv.getName().equals(propertyName));

        return pv;
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        } else {
            Iterator var = this.propertyValueList.iterator();

            while(true) {
                PropertyValue newPv;
                PropertyValue pvOld;
                do {
                    if (!var.hasNext()) {
                        return changes;
                    }

                    newPv = (PropertyValue)var.next();
                    pvOld = old.getPropertyValue(newPv.getName());
                } while(pvOld != null && pvOld.equals(newPv));

                changes.addPropertyValue(newPv);
            }
        }
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for(int i = 0; i < this.propertyValueList.size(); ++i) {
            PropertyValue currentPv = (PropertyValue)this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                pv = this.mergeIfRequired(pv, currentPv);
                this.setPropertyValueAt(pv, i);
                return this;
            }
        }

        this.propertyValueList.add(pv);
        return this;
    }
    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }
    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            Mergeable mergeable = (Mergeable)value;
            if (mergeable.isMergeEnabled()) {
                Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv, merged);
            }
        }

        return newPv;
    }
    @Override
    public boolean contains(String propertyName) {
        return this.getPropertyValue(propertyName) != null || this.processedProperties != null && this.processedProperties.contains(propertyName);
    }

    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

}
