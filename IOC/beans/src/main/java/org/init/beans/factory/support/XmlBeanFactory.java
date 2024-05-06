package org.init.beans.factory.support;

import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.core.io.Resource;

public class XmlBeanFactory extends DefaultListableBeanFactory{
    private final XmlBeanDefinitionReader reader;
    public XmlBeanFactory(Resource resource) throws BeansException {
        this(resource, (BeanFactory)null);
    }
    public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
        this.reader = new XmlBeanDefinitionReader(this);
        this.reader.loadBeanDefinitions(resource);
    }


    public void loadBeanDefinitions(Resource resource) throws BeansException {
        this.reader.loadBeanDefinitions(resource);
    }
}
