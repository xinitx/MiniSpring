package org.init.mvc.context.support;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.support.AbstractApplicationContext;
import org.init.core.io.Resource;
import org.init.mvc.context.ConfigurableWebApplicationContext;
import org.init.context.event.ApplicationListener;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.io.ClassPathXmlResource;
import org.init.core.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;

public class XmlWebApplicationContext extends AbstractApplicationContext implements ConfigurableWebApplicationContext {
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";
    @Nullable
    private String configLocation;
    @Nullable
    private ServletContext servletContext;
    @Nullable
    private ServletConfig servletConfig;

    @Nullable
    private DefaultListableBeanFactory beanFactory;

    protected final void refreshBeanFactory() throws BeansException {
        try {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            this.loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        } catch (IOException var5) {
            throw new RuntimeException("I/O error parsing bean definition source for " + configLocation, var5);
        }
    }
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        Resource resource = new ClassPathXmlResource(this.configLocation);
        beanDefinitionReader.loadBeanDefinitions(resource);
    }

    @Override
    public boolean isCurrentlyInCreation(String var1) {
        return false;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return null;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    @Override
    public void setId(String var1) {

    }

    @Override
    public void setEnvironment(ConfigurableEnvironment var1) {

    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {

    }

    @Override
    public void registerShutdownHook() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void setServletContext(ServletContext var1) {
        this.servletContext = var1;
    }

    @Override
    public void setServletConfig(ServletConfig var1) {
        this.servletConfig = var1;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public void setNamespace(String var1) {

    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public void setConfigLocation(String var1) {
        this.configLocation = var1;
    }

    @Override
    public void setConfigLocations(String... var1) {

    }

    @Override
    public String[] getConfigLocations() {
        return new String[0];
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }
}
