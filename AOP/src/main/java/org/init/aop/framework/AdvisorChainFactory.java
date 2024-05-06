package org.init.aop.framework;

import java.lang.reflect.Method;
import java.util.List;

public interface AdvisorChainFactory {
    List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised var1, Method var2, Class<?> var3);
}
