package org.init.aop.framework.adapter;

import org.init.aop.MethodBeforeAdvice;
import org.init.aop.framework.MethodInterceptor;
import org.init.aop.framework.MethodInvocation;

import java.io.Serializable;

public class MethodBeforeAdviceInterceptor  implements MethodInterceptor, Serializable {
    private MethodBeforeAdvice advice;
    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
