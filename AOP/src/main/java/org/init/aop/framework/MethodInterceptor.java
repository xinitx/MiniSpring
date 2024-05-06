package org.init.aop.framework;

public interface MethodInterceptor extends Interceptor  {
    Object invoke(MethodInvocation var1) throws Throwable;
}
