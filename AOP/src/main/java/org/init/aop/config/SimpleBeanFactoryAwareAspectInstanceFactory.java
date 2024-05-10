package org.init.aop.config;

import org.init.aop.aspectj.AspectInstanceFactory;
import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryAware;
import org.init.beans.factory.config.ConfigurableBeanFactory;
import org.init.core.util.ClassUtils;

public class SimpleBeanFactoryAwareAspectInstanceFactory implements AspectInstanceFactory, BeanFactoryAware {
    private String aspectBeanName;
    private BeanFactory beanFactory;
    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object getAspectInstance() {
        try {
            return this.beanFactory.getBean(this.aspectBeanName);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassLoader getAspectClassLoader() {
        return this.beanFactory instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory)this.beanFactory).getBeanClassLoader() : ClassUtils.getDefaultClassLoader();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
