package org.init.beans.factory.support;

import org.init.beans.BeansException;
import org.init.beans.BeanDefinition;
import org.init.beans.factory.BeanPostProcessor;
import org.init.beans.factory.InstantiationAwareBeanPostProcessor;
import org.init.beans.factory.config.ConfigurableBeanFactory;
import org.init.core.util.ClassUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    private final Map<String, BeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap(256);
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList();
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new ThreadLocal<>();
    private boolean hasInstantiationAwareBeanPostProcessors;
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Override
    public Object getBean(String beanName) throws BeansException, ClassNotFoundException {
        Object instance = this.getSingleton(beanName);

        if (instance == null) {
            System.out.println("get bean null -------------- " + beanName);
            BeanDefinition bd = this.getMergedLocalBeanDefinition(beanName);
            if(bd.isSingleton()){
                instance = this.getSingleton(beanName, () -> {
                    try {
                        return this.createBean(bd);
                    } catch (BeansException | ClassNotFoundException var5) {
                        throw var5;
                    }
                });
            }else if (bd.isPrototype()){
                this.beforePrototypeCreation(beanName);
                instance = this.createBean(bd);
                this.afterPrototypeCreation(beanName);
            }
        }
        if (instance == null) {
            throw new BeansException("bean is null.");
        }
        return instance;
    }

    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set)curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet(2);
            beanNameSet.add((String)curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set)curVal;
            beanNameSet.add(beanName);
        }
    }

    protected BeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        BeanDefinition bd = this.mergedBeanDefinitions.get(beanName);
        return bd != null ? bd : this.getMergedBeanDefinition(beanName, this.getBeanDefinition(beanName));
    }

    protected BeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeansException {
        BeanDefinition bd = null;
        synchronized (this.mergedBeanDefinitions){
            bd = this.mergedBeanDefinitions.get(beanName);
            if (bd == null) {
                //使用克隆是为了线程安全，比如先创建bean然后修改了BeanDefinition，创建的bean应该是基于原始的BeanDefinition
                bd = beanDefinition.cloneBeanDefinition();
                this.mergedBeanDefinitions.put(beanName, bd);
            }
        }
        if (bd == null){
            throw new BeansException("bean is null.");
        }
        return bd;
    }

    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name);
    }

    @Override
    public boolean isSingleton(String name) throws BeansException {
        return this.getMergedLocalBeanDefinition(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) throws BeansException {
        return this.getMergedLocalBeanDefinition(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) throws BeansException {
        try {
            return Class.forName(this.getMergedLocalBeanDefinition(name).getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
        if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
            this.hasInstantiationAwareBeanPostProcessors = true;
        }
    }
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
    @Override
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }
    protected abstract Object createBean(BeanDefinition bd) throws BeansException, ClassNotFoundException;
    protected abstract BeanDefinition getBeanDefinition(String var1) throws BeansException;
}
