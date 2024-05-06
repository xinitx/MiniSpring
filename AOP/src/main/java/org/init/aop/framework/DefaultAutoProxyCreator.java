package org.init.aop.framework;

import org.init.aop.Advisor;
import org.init.aop.aspectj.AspectJPointcutAdvisor;
import org.init.beans.BeansException;

import java.util.Iterator;
import java.util.List;

public class DefaultAutoProxyCreator  extends AbstractAdvisorAutoProxyCreator {
    protected boolean shouldSkip(Class<?> beanClass, String beanName){
        List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
        Iterator var = candidateAdvisors.iterator();

        Advisor advisor;
        do {
            if (!var.hasNext()) {
                return super.shouldSkip(beanClass, beanName);
            }
            advisor = (Advisor)var.next();
        } while(!(advisor instanceof AspectJPointcutAdvisor) || !((AspectJPointcutAdvisor)advisor).getAspectName().equals(beanName));
        return true;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
