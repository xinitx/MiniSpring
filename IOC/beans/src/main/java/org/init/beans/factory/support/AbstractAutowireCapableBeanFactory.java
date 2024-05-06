package org.init.beans.factory.support;

import org.init.beans.BeanDefinition;
import org.init.beans.BeansException;
import org.init.beans.factory.*;
import org.init.beans.factory.config.ConstructorArgumentValue;
import org.init.beans.factory.config.PropertyValue;
import org.init.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;

        Object current;
        for(Iterator var4 = this.getBeanPostProcessors().iterator(); var4.hasNext(); result = current) {
            BeanPostProcessor beanProcessor = (BeanPostProcessor)var4.next();
            current = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
        }

        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;

        Object current;
        for(Iterator var4 = this.getBeanPostProcessors().iterator(); var4.hasNext(); result = current) {
            BeanPostProcessor beanProcessor = (BeanPostProcessor)var4.next();
            current = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
        }

        return result;
    }


    protected Object createBean(BeanDefinition bd) throws BeansException, ClassNotFoundException {

        Object obj = this.resolveBeforeInstantiation(bd.getId(), bd);
        if (obj != null) {
            return obj;
        }
        obj = doCreateBean(bd.getId(),bd);
        return obj;
    }

    private Object resolveBeforeInstantiation(String id, BeanDefinition bd) throws BeansException, ClassNotFoundException {
        Object bean = null;
        Class<?> targetType = bd.getResolvedTargetType();
        if(targetType != null){
            bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, id);
            if (bean != null) {
                bean = this.applyBeanPostProcessorsAfterInitialization(bean, id);
            }
        }
        return bean;
    }

    private Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        Iterator var3 = this.getBeanPostProcessors().iterator();

        while(var3.hasNext()) {
            BeanPostProcessor bp = (BeanPostProcessor)var3.next();
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private void populateBean(BeanDefinition bd, Class<?> clz, Object obj) {
        handleProperties(bd, clz, obj);
    }

    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        System.out.println("handle properties for bean : " + bd.getId());
        List<PropertyValue> propertyValues = bd.getPropertyValues();
        if (propertyValues != null && !propertyValues.isEmpty()) {
            for (int i=0; i<propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues =   new Object[1];
                if (!isRef) {
                    if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                        paramTypes[0] = String.class;
                    }
                    else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    }
                    else if ("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    }
                    else {
                        paramTypes[0] = String.class;
                    }

                    paramValues[0] = pValue;
                }
                else {
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        paramValues[0] = getBean((String)pValue);

                    } catch (BeansException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);

                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    method.invoke(obj, paramValues);
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

    private Object doCreateBean(String beanName, BeanDefinition bd) throws BeansException {
        Class<?> clz = null;

        Object bean = this.createBeanInstance(beanName, bd);
        if (bd.isSingleton()) {
            if(!this.singletonObjects.containsKey(beanName)){
                this.singletonFactories.put(bd.getId(), ()->{
                    return this.getEarlyBeanReference(beanName, bd, bean);
                });
            }
        }

        try {

            clz = Class.forName(bd.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Object exposedObject = bean;
        populateBean(bd, clz, exposedObject);
        exposedObject = this.initializeBean(beanName, exposedObject, bd);
        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + bean.toString());
        return exposedObject;
    }

    private Object getEarlyBeanReference(String beanName, BeanDefinition bd, Object instance) throws BeansException {
        Object exposedObject = instance;
        Iterator var5 = this.getBeanPostProcessors().iterator();

        while(var5.hasNext()) {
            BeanPostProcessor bp = (BeanPostProcessor)var5.next();
            if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor)bp;
                exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }

    protected Object  initializeBean(String beanName, Object instance, BeanDefinition bd) throws BeansException {
        this.invokeAwareMethods(beanName, instance);
        Object wrappedBean = instance;
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(instance, beanName);
        this.invokeInitMethod(bd, instance);
        wrappedBean = applyBeanPostProcessorsAfterInitialization(instance, beanName);
        return wrappedBean;
    }

    private void invokeAwareMethods(String beanName, Object bean) throws BeansException {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                BeanNameAware beanNameAware = (BeanNameAware)bean;
                beanNameAware.setBeanName(beanName);
            }

            if (bean instanceof BeanClassLoaderAware) {
                BeanClassLoaderAware beanClassLoaderAware = (BeanClassLoaderAware)bean;
                ClassLoader bcl = this.getBeanClassLoader();
                if (bcl != null) {
                    beanClassLoaderAware.setBeanClassLoader(bcl);
                }
            }


            if (bean instanceof BeanFactoryAware) {
                BeanFactoryAware beanFactoryAware = (BeanFactoryAware)bean;
                beanFactoryAware.setBeanFactory(this);
            }
        }
    }

    protected void invokeInitMethod(BeanDefinition bd, Object instance) {
        boolean isInitializingBean = instance instanceof InitializingBean;
        if(isInitializingBean){
            try {
                ((InitializingBean)instance).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (bd.getInitMethodName() != null && !bd.getInitMethodName().equals("")) {
            Class<?> clz = bd.getClass();
            Method method = null;
            try {
                method = clz.getMethod(bd.getInitMethodName());
                method.invoke(instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected Object createBeanInstance(String beanName, BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            clz = Class.forName(bd.getClassName());


            List<ConstructorArgumentValue> argumentValues = bd.getConstructorArgumentValues();
            if (argumentValues != null && !argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.size()];
                Object[] paramValues =   new Object[argumentValues.size()];
                for (int i=0; i<argumentValues.size(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.get(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                    else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    }
                    else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue()).intValue();
                    }
                    else {
                        if ("BeanDefinition".equals(argumentValue.getType())){
                            BeanDefinition abd = (BeanDefinition)argumentValue.getValue();
                            Object bean = createBean(abd);
                            paramTypes[i] = abd.getResolvedTargetType();
                            paramValues[i] = bean;
                        }
                        else if (argumentValue.getName().startsWith("&")){
                            BeanDefinition abd = (BeanDefinition)argumentValue.getValue();
                            Object bean = createBean(abd);
                            paramTypes[i] = abd.getResolvedTargetType();
                            try {
                                paramValues[i] = ((FactoryBean<?>)bean).getObject();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            Object bean = getBean(argumentValue.getName());
                            paramTypes[i] = Class.forName(argumentValue.getType());
                            paramValues[i] = bean;
                        }
                    }
                }
                try {
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                con = clz.getConstructor();
                obj = clz.newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }


}
