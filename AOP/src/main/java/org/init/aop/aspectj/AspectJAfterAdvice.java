package org.init.aop.aspectj;

import org.init.aop.AfterAdvice;
import org.init.aop.framework.MethodInterceptor;
import org.init.aop.framework.MethodInvocation;

import java.io.Serializable;
import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor, AfterAdvice, Serializable {


    public AspectJAfterAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, pointcut, aspectInstanceFactory);
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return true;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object var;
        try {
            var = mi.proceed();
        } finally {
            this.invokeAdviceMethod();
        }

        return var;
    }
}
