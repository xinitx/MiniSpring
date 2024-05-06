package org.init.aop.config;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryAware;
import org.init.beans.factory.FactoryBean;
import org.init.core.util.StringUtils;

import java.lang.reflect.Method;

public class MethodLocatingFactoryBean implements FactoryBean<Method>, BeanFactoryAware {

    private String targetBeanName;

    private String methodName;

    private Method method;

    public MethodLocatingFactoryBean() {
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public Method getObject() throws Exception {
        return this.method;
    }

    public Class<Method> getObjectType() {
        return Method.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!StringUtils.hasText(this.targetBeanName)) {
            throw new IllegalArgumentException("Property 'targetBeanName' is required");
        } else if (!StringUtils.hasText(this.methodName)) {
            throw new IllegalArgumentException("Property 'methodName' is required");
        } else {
            Class<?> beanClass = beanFactory.getType(this.targetBeanName);
            if (beanClass == null) {
                throw new IllegalArgumentException("Can't determine type of bean with name '" + this.targetBeanName + "'");
            } else {
                try {
                    this.method = beanClass.getMethod(this.methodName);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                if (this.method == null) {
                    throw new IllegalArgumentException("Unable to locate method [" + this.methodName + "] on bean [" + this.targetBeanName + "]");
                }
            }
        }
    }
}

