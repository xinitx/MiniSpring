package org.init.mvc.context.support;

import org.init.beans.BeansException;
import org.init.beans.factory.BeanPostProcessor;
import org.init.core.lang.Nullable;
import org.init.mvc.context.ServletConfigAware;
import org.init.mvc.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletContextAwareProcessor implements BeanPostProcessor {
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServletConfig servletConfig;

    protected ServletContextAwareProcessor() {
    }

    public ServletContextAwareProcessor(ServletContext servletContext) {
        this(servletContext, (ServletConfig)null);
    }

    public ServletContextAwareProcessor(ServletConfig servletConfig) {
        this((ServletContext)null, servletConfig);
    }

    public ServletContextAwareProcessor(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
        this.servletContext = servletContext;
        this.servletConfig = servletConfig;
    }

    @Nullable
    protected ServletContext getServletContext() {
        return this.servletContext == null && this.getServletConfig() != null ? this.getServletConfig().getServletContext() : this.servletContext;
    }

    @Nullable
    protected ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.getServletContext() != null && bean instanceof ServletContextAware) {
            ((ServletContextAware)bean).setServletContext(this.getServletContext());
        }

        if (this.getServletConfig() != null && bean instanceof ServletConfigAware) {
            ((ServletConfigAware)bean).setServletConfig(this.getServletConfig());
        }

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
