package org.init.aop;

public interface Advisor {
    Advice EMPTY_ADVICE = new Advice() {
    };

    Advice getAdvice();

    boolean isPerInstance();
}
