package org.init.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice{
    void before(Method var1, Object[] var2, Object var3) throws Throwable;
}
