package org.init.aop.framework;

import org.init.aop.Advice;
import org.init.aop.Advisor;
import org.init.aop.Pointcut;
import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryAware;
import org.init.beans.factory.SmartInstantiationAwareBeanPostProcessor;
import org.init.core.util.*;
import org.init.aop.target.SingletonTargetSource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractAutoProxyCreator extends ProxyProcessorSupport implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware {
    protected static final Object[] DO_NOT_PROXY = null;
    private BeanFactory beanFactory;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private final Set<Object> earlyProxyReferences = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Set<String> targetSourcedBeans = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Map<Object, Boolean> advisedBeans = new ConcurrentHashMap(256);
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    protected BeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        Object cacheKey = bean.getClass();
        if (!this.earlyProxyReferences.contains(cacheKey)) {
            this.earlyProxyReferences.add(cacheKey);
        }
        return this.wrapIfNecessary(bean, beanName, cacheKey);
    }
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        Object cacheKey = beanClass;
        if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
            if (this.advisedBeans.containsKey(cacheKey)) {
                return null;
            }

            if (this.isInfrastructureClass(beanClass) || this.shouldSkip(beanClass, beanName)) {
                this.advisedBeans.put(cacheKey, Boolean.FALSE);
                return null;
            }
        }
        return null;
    }

    public Object postProcessAfterInitialization( Object bean, String beanName) throws BeansException {
        if (bean != null) {
            Object cacheKey = bean.getClass();
            if (!this.earlyProxyReferences.contains(cacheKey)) {
                return this.wrapIfNecessary(bean, beanName, cacheKey);
            }
        }

        return bean;
    }

    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) throws BeansException {
        if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
            return bean;
        } else if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
            return bean;
        } else if (!this.isInfrastructureClass(bean.getClass()) && !this.shouldSkip(bean.getClass(), beanName)) {
            Object[] specificInterceptors = null;
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if(interfaces != null && interfaces.length > 0){
                specificInterceptors = this.getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, (TargetSource)null);
            }
            if (specificInterceptors != DO_NOT_PROXY) {
                this.advisedBeans.put(cacheKey, Boolean.TRUE);
                Object proxy = this.createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
                return proxy;
            } else {
                this.advisedBeans.put(cacheKey, Boolean.FALSE);
                return bean;
            }
        } else {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return bean;
        }
    }
    protected Object createProxy(Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
        ProxyFactory proxyFactory = new ProxyFactory();
        this.evaluateProxyInterfaces(beanClass, proxyFactory);
        proxyFactory.setTargetSource(targetSource);
        for (Object advisor :specificInterceptors){
            proxyFactory.addAdvisor((Advisor) advisor);
        }
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(proxyFactory);
        return proxy.getProxy(this.getProxyClassLoader());
    }
    protected void evaluateProxyInterfaces(Class<?> beanClass, ProxyFactory proxyFactory) {
        Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, this.getProxyClassLoader());
        for(Class ifc : targetInterfaces) {
            proxyFactory.addInterface(ifc);
        }
    }
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass) || AopInfrastructureBean.class.isAssignableFrom(beanClass);
        return retVal;
    }
    protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        return false;
    }
    protected ClassLoader getProxyClassLoader() {
        return this.proxyClassLoader;
    }
    protected abstract Object[] getAdvicesAndAdvisorsForBean(Class<?> var1, String var2,  TargetSource var3) throws BeansException;
}
