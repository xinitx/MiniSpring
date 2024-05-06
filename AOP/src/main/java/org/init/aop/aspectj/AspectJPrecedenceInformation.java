package org.init.aop.aspectj;

import org.init.core.Ordered;

public interface AspectJPrecedenceInformation extends Ordered {
    String getAspectName();

    int getDeclarationOrder();

    boolean isBeforeAdvice();

    boolean isAfterAdvice();
}
