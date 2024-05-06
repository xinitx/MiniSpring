package org.init.aop.aspectj;

import org.init.aop.Advice;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice, AspectJPrecedenceInformation, Serializable {
    private final Class<?> declaringClass;
    private final Class<?>[] parameterTypes;
    private String methodName;
    private String aspectName;
    protected transient Method aspectJAdviceMethod;
    private final AspectJExpressionPointcut pointcut;
    private final AspectInstanceFactory aspectInstanceFactory;
    public AbstractAspectJAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
        this.aspectInstanceFactory = aspectInstanceFactory;
    }
    @Override
    public String getAspectName() {
        return aspectName;
    }
    public void setAspectName(String name) {
        this.aspectName = name;
    }

    @Override
    public int getDeclarationOrder() {
        return 0;
    }
    @Override
    public int getOrder() {
        return 0;
    }

    public AspectJExpressionPointcut getPointcut() {
        return pointcut;
    }

    protected void invokeAdviceMethod() {
        try {
            aspectJAdviceMethod.invoke(aspectInstanceFactory.getAspectInstance(), null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
