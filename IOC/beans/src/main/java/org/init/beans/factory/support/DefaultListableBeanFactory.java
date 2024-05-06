package org.init.beans.factory.support;

import org.init.beans.BeansException;
import org.init.beans.BeanDefinition;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);
    private volatile List<String> beanDefinitionNames = new ArrayList(256);
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionNames.contains(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionNames.toArray(new String[0]);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) throws BeansException {
        List<String> result = new ArrayList<>();

        for (String beanName : this.beanDefinitionNames) {
            boolean matchFound = false;
            BeanDefinition mbd = this.getBeanDefinition(beanName);
            Class<?> classToMatch = mbd.getBeanClass();
            if (type.isAssignableFrom(classToMatch)) {
                matchFound = true;
            }
            else {
                matchFound = false;
            }

            if (matchFound) {
                result.add(beanName);
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException, ClassNotFoundException {
        String[] beanNames = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNames.length);
        for (String beanName : beanNames) {
            Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name,bd);
        this.beanDefinitionNames.add(name);
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        List<String> beanNames = new ArrayList(this.beanDefinitionNames);
        for (String beanName : beanNames){
            BeanDefinition bd = this.getBeanDefinition(beanName);
            if(bd.isSingleton()){
                try {
                    this.getBean(beanName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
