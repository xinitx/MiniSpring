package org.init.test;

import org.init.beans.BeansException;
import org.init.beans.factory.support.XmlBeanFactory;
import org.init.core.io.ClassPathXmlResource;

public class BeanFactoryTest {
    public static void main(String[] args) {

        try {
            XmlBeanFactory beanFactory = new XmlBeanFactory(new ClassPathXmlResource("beans.xml"));
            TestServiceAImpl serviceA = (TestServiceAImpl)beanFactory.getBean("serviceA");
            TestServiceB serviceB = (TestServiceB)beanFactory.getBean("serviceB");
            serviceA.sayHello();
            serviceB.getTestService().sayHello();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
