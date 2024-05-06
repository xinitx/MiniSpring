package org.init.beans;

import org.init.beans.factory.config.ConstructorArgumentValue;
import org.init.beans.factory.config.PropertyValue;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    private boolean lazyInit = true;
    private String[] dependsOn;
    private String initMethodName;

    private List<ConstructorArgumentValue> constructorArgumentValues;

    private List<PropertyValue> propertyValues;
    private volatile Object beanClass;
    private String id;
    private String className;
    volatile Class<?> resolvedTargetType;
    private String scope = SCOPE_SINGLETON;


    public BeanDefinition(BeanDefinition beanDefinition) {
        this.id = beanDefinition.getId();
        this.className = beanDefinition.getClassName();
        this.scope = beanDefinition.getScope();
        this.lazyInit = beanDefinition.isLazyInit();
        this.dependsOn = beanDefinition.getDependsOn();
        this.initMethodName = beanDefinition.getInitMethodName();
        this.constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        this.propertyValues = beanDefinition.getPropertyValues();
        this.beanClass = beanDefinition.getBeanClass();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass(){
        if (this.beanClass == null){
            try {
                this.beanClass = Class.forName(this.className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return (Class<?>) this.beanClass;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String[] getDependsOn() {
        return this.dependsOn;
    }

    public void setConstructorArgumentValues(List<ConstructorArgumentValue> constructorArgumentValues) {
        this.constructorArgumentValues =
                (constructorArgumentValues != null ? constructorArgumentValues : new ArrayList<ConstructorArgumentValue>());
    }

    public List<ConstructorArgumentValue> getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgumentValues.isEmpty();
    }
    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = (propertyValues != null ? propertyValues : new ArrayList<PropertyValue>());
    }

    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getInitMethodName() {
        return this.initMethodName;
    }

    public BeanDefinition cloneBeanDefinition() {
        return new BeanDefinition(this);
    }

    public Class<?> getResolvedTargetType() throws ClassNotFoundException {
        if(resolvedTargetType == null){
            resolvedTargetType = Class.forName(className);
        }
        return resolvedTargetType;
    }

    public void setResolvedTargetType(Class<?> resolvedTargetType) {
        this.resolvedTargetType = resolvedTargetType;
    }
}
