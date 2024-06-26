package org.init.core.env;

import org.init.core.lang.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class MutablePropertySources implements PropertySources {
    private final List<PropertySource<?>> propertySourceList;

    public MutablePropertySources() {
        this.propertySourceList = new CopyOnWriteArrayList();
    }

    public MutablePropertySources(PropertySources propertySources) {
        this();
        Iterator var2 = propertySources.iterator();

        while(var2.hasNext()) {
            PropertySource<?> propertySource = (PropertySource)var2.next();
            this.addLast(propertySource);
        }

    }

    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }

    public Spliterator<PropertySource<?>> spliterator() {
        return Spliterators.spliterator(this.propertySourceList, 0);
    }

    public Stream<PropertySource<?>> stream() {
        return this.propertySourceList.stream();
    }

    public boolean contains(String name) {
        Iterator var2 = this.propertySourceList.iterator();

        PropertySource propertySource;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            propertySource = (PropertySource)var2.next();
        } while(!propertySource.getName().equals(name));

        return true;
    }

    @Nullable
    public PropertySource<?> get(String name) {
        Iterator var2 = this.propertySourceList.iterator();

        PropertySource propertySource;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            propertySource = (PropertySource)var2.next();
        } while(!propertySource.getName().equals(name));

        return propertySource;
    }

    public void addFirst(PropertySource<?> propertySource) {
        synchronized(this.propertySourceList) {
            this.removeIfPresent(propertySource);
            this.propertySourceList.add(0, propertySource);
        }
    }

    public void addLast(PropertySource<?> propertySource) {
        synchronized(this.propertySourceList) {
            this.removeIfPresent(propertySource);
            this.propertySourceList.add(propertySource);
        }
    }

    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized(this.propertySourceList) {
            this.removeIfPresent(propertySource);
            int index = this.assertPresentAndGetIndex(relativePropertySourceName);
            this.addAtIndex(index, propertySource);
        }
    }

    public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
        this.assertLegalRelativeAddition(relativePropertySourceName, propertySource);
        synchronized(this.propertySourceList) {
            this.removeIfPresent(propertySource);
            int index = this.assertPresentAndGetIndex(relativePropertySourceName);
            this.addAtIndex(index + 1, propertySource);
        }
    }

    public int precedenceOf(PropertySource<?> propertySource) {
        return this.propertySourceList.indexOf(propertySource);
    }

    @Nullable
    public PropertySource<?> remove(String name) {
        synchronized(this.propertySourceList) {
            int index = this.propertySourceList.indexOf(PropertySource.named(name));
            return index != -1 ? (PropertySource)this.propertySourceList.remove(index) : null;
        }
    }

    public void replace(String name, PropertySource<?> propertySource) {
        synchronized(this.propertySourceList) {
            int index = this.assertPresentAndGetIndex(name);
            this.propertySourceList.set(index, propertySource);
        }
    }

    public int size() {
        return this.propertySourceList.size();
    }

    public String toString() {
        return this.propertySourceList.toString();
    }

    protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
        String newPropertySourceName = propertySource.getName();
        if (relativePropertySourceName.equals(newPropertySourceName)) {
            throw new IllegalArgumentException("PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
        }
    }

    protected void removeIfPresent(PropertySource<?> propertySource) {
        this.propertySourceList.remove(propertySource);
    }

    private void addAtIndex(int index, PropertySource<?> propertySource) {
        this.removeIfPresent(propertySource);
        this.propertySourceList.add(index, propertySource);
    }

    private int assertPresentAndGetIndex(String name) {
        int index = this.propertySourceList.indexOf(PropertySource.named(name));
        if (index == -1) {
            throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
        } else {
            return index;
        }
    }
}
