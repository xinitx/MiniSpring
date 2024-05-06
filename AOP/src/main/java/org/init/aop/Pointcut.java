package org.init.aop;

public interface Pointcut {
    //Pointcut TRUE = TruePointcut.INSTANCE;
    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
