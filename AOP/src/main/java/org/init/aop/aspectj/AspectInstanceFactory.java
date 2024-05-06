package org.init.aop.aspectj;

import org.init.core.Ordered;

public interface AspectInstanceFactory extends Ordered {
    Object getAspectInstance();

    ClassLoader getAspectClassLoader();
}
