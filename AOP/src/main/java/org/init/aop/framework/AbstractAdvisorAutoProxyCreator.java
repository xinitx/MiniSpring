package org.init.aop.framework;

import org.init.aop.Advisor;
import org.init.aop.Pointcut;
import org.init.aop.PointcutAdvisor;
import org.init.beans.BeanFactory;
import org.init.beans.factory.config.ConfigurableListableBeanFactory;
import org.init.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractAdvisorAutoProxyCreator extends AbstractAutoProxyCreator {
    private BeanFactoryAdvisorRetrievalHelper advisorRetrievalHelper;
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        if(beanFactory instanceof ConfigurableListableBeanFactory){
            this.advisorRetrievalHelper = new BeanFactoryAdvisorRetrievalHelper((ConfigurableListableBeanFactory)beanFactory);
        }
    }
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
        List<Advisor> advisors = this.findEligibleAdvisors(beanClass, beanName);
        return advisors.isEmpty() ? DO_NOT_PROXY : advisors.toArray();
    }
    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = this.findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        this.extendAdvisors(eligibleAdvisors);
        if (!eligibleAdvisors.isEmpty()) {
            eligibleAdvisors = this.sortAdvisors(eligibleAdvisors);
        }

        return eligibleAdvisors;
    }
    protected List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        //ProxyCreationContext.setCurrentProxiedBeanName(beanName);
        try {
            if (candidateAdvisors.isEmpty()) {
                return candidateAdvisors;
            } else {
                List<Advisor> eligibleAdvisors = new ArrayList();
                Iterator var3 = candidateAdvisors.iterator();
                while(var3.hasNext()) {
                    Advisor candidate = (Advisor)var3.next();
                    if ( canApply(candidate, beanClass)) {
                        eligibleAdvisors.add(candidate);
                    }
                }
                return eligibleAdvisors;
            }
        } finally {
            //ProxyCreationContext.setCurrentProxiedBeanName((String)null);
        }
    }
    public static boolean canApply(Advisor advisor, Class<?> targetClass) {
        if (advisor instanceof PointcutAdvisor ) {
            return canApply(((PointcutAdvisor)advisor).getPointcut(), targetClass);
        } else {
            return false;
        }
    }
    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        return true;
        /*if (!pc.getClassFilter().matches(targetClass)) {
            return true;
        } else {
            return true;
        }*/
    }
    protected List<Advisor> findCandidateAdvisors() {
        System.out.println("findCandidateAdvisors");
        return this.advisorRetrievalHelper.findAdvisorBeans();
    }
    protected List<Advisor> sortAdvisors(List<Advisor> advisors) {
        // 按order排序
        return advisors;
    }
    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    }
}
