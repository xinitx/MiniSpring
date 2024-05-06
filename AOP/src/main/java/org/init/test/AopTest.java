package org.init.test;

import org.init.beans.BeansException;
import org.init.beans.factory.support.XmlBeanFactory;
import org.init.context.ClassPathXmlApplicationContext;
import org.init.core.io.ClassPathXmlResource;
import org.init.aop.framework.DefaultAutoProxyCreator;

public class AopTest {
    public static void main(String[] args) throws BeansException, ClassNotFoundException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        TestInterface testProxyCircle = (TestInterface) context.getBean("circle");
        testProxyCircle.test();
    }
}
