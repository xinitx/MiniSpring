package org.init.test;

import org.init.beans.BeansException;
import org.init.beans.XmlBeanDefinitionReader;
import org.init.beans.factory.support.DefaultListableBeanFactory;
import org.init.context.ClassPathXmlApplicationContext;


public class AopTest {
    public static void main(String[] args) throws BeansException, ClassNotFoundException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        TestInterface testProxyCircle = (TestInterface) context.getBean("circle");
        testProxyCircle.test();
    }
}
