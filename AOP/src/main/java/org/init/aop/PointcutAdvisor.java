package org.init.aop;

public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
