package org.init.aop.framework;

import org.init.aop.Advice;
import org.init.aop.Advisor;
import org.init.aop.MethodBeforeAdvice;
import org.init.aop.aspectj.AspectJMethodBeforeAdvice;
import org.init.aop.framework.adapter.MethodBeforeAdviceInterceptor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised var1, Method method, Class<?> targetClass) {
        List<Object> cache = new ArrayList<>();
        List<Advisor> advisors = ((AdvisedSupport)var1).getAdvisors();
        for (Advisor advisor : advisors){
            Advice advice = advisor.getAdvice();
            if (advice instanceof MethodInterceptor){
                cache.add(advice);
            }else if (advice instanceof AspectJMethodBeforeAdvice){
                cache.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice)advice));
            }
        }
        return cache;
    }
}
