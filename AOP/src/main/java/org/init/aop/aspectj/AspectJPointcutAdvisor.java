package org.init.aop.aspectj;

import org.init.aop.Advice;
import org.init.aop.Pointcut;
import org.init.aop.PointcutAdvisor;
import org.init.core.Ordered;

public class AspectJPointcutAdvisor implements PointcutAdvisor, Ordered {
    private final AbstractAspectJAdvice advice;
    private final Pointcut pointcut;
    private final String aspectName;
    public AspectJPointcutAdvisor(AbstractAspectJAdvice advice) {
        this.advice = advice;
        this.aspectName = advice.getAspectName();
        this.pointcut = advice.getPointcut();
    }

    public String getAspectName() {
        return aspectName;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public boolean isPerInstance() {
        return false;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}
