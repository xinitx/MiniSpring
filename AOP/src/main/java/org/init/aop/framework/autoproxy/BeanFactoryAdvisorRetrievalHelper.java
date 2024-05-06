package org.init.aop.framework.autoproxy;

import org.init.aop.Advisor;
import org.init.beans.BeansException;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.LinkedList;
import java.util.List;

public class BeanFactoryAdvisorRetrievalHelper {
    private final ConfigurableListableBeanFactory beanFactory;
    private String[] cachedAdvisorBeanNames;
    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    public List<Advisor> findAdvisorBeans() {
        String[] advisorNames = null;
        synchronized(this) {
            advisorNames = this.cachedAdvisorBeanNames;
            if (advisorNames == null) {
                try {
                    advisorNames = beanFactory.getBeanNamesForType(Advisor.class);
                    this.cachedAdvisorBeanNames = advisorNames;
                } catch (BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (advisorNames.length == 0) {
            return new LinkedList();
        }else {
            List<Advisor> advisors = new LinkedList();
            for (String name : advisorNames) {
                if (this.beanFactory.isCurrentlyInCreation(name)) {
                    System.out.println("Skipping currently created advisor '" + name + "'");
                }else{
                    try {
                        advisors.add((Advisor) this.beanFactory.getBean(name));
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return advisors;
        }


    }
}
