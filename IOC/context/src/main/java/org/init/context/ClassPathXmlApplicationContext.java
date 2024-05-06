package org.init.context;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.event.ApplicationListener;
import org.init.context.support.AbstractApplicationContext;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.io.ClassPathXmlResource;
import org.init.core.io.Resource;
import org.init.core.util.ClassUtils;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    private Resource[] configResources;
    DefaultListableBeanFactory beanFactory;
    public ClassPathXmlApplicationContext(String fileName){
        this(fileName, true);
    }

    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh){
        Resource res = new ClassPathXmlResource(fileName);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        reader.loadBeanDefinitions(res);

        this.beanFactory = bf;

        if (isRefresh) {
            try {
                refresh();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {

    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
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
    public boolean isCurrentlyInCreation(String var1) {
        return this.isCurrentlyInCreation(var1);
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return ClassUtils.getDefaultClassLoader();
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
}
