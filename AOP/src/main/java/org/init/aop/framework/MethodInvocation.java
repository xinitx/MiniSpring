package org.init.aop.framework;

import java.lang.reflect.Method;

public interface MethodInvocation extends Invocation  {
	Method getMethod();
}
