package org.init.context.support;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.Aware;
import org.init.beans.factory.BeanPostProcessor;
import org.init.context.ApplicationContextAware;
import org.init.context.ConfigurableApplicationContext;
import org.init.context.EnvironmentAware;
import org.init.core.lang.Nullable;

import java.security.AccessControlContext;
import java.security.AccessController;

class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //应有权限判断

        this.invokeAwareInterfaces(bean);
        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware)bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof ApplicationContextAware) {
                try {
                    ((ApplicationContextAware)bean).setApplicationContext(this.applicationContext);
                } catch (BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }


}
