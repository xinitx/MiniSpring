package org.init.test;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.core.io.ClassPathResource;


public class BeanFactoryTest {
    public static void main(String[] args) {

        try {
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
            reader.loadBeanDefinitions("beans.xml");
            TestServiceAImpl serviceA = (TestServiceAImpl)beanFactory.getBean("serviceA");
            TestServiceB serviceB = (TestServiceB)beanFactory.getBean("serviceB");
            serviceA.sayHello();
            serviceB.getTestService().sayHello();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }
}
