package org.init.test;

import org.init.beans.BeansException;
import org.init.context.ClassPathXmlApplicationContext;

public class ApplicationContextTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        try {
            TestServiceAImpl serviceA = (TestServiceAImpl)context.getBean("serviceA");
            TestServiceB serviceB = (TestServiceB)context.getBean("serviceB");
            serviceA.sayHello();
            serviceB.getTestService().sayHello();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }
}
