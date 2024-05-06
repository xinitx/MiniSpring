package org.init.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation{
	protected final Object proxy;
	protected final Object target;
	protected final Method method;
	protected Object[] arguments;
	private Class<?> targetClass;
	protected final List<?> interceptors;
	private int currentInterceptorIndex = -1;

	protected ReflectiveMethodInvocation(
			Object proxy, Object target, Method method, Object[] arguments,
			Class<?> targetClass, List<Object> interceptors) {
		this.proxy = proxy;
		this.target = target;
		this.targetClass = targetClass;
		this.method = method;
		this.arguments = arguments;
		this.interceptors = interceptors;
	}

	public final Object getProxy() {
		return this.proxy;
	}

	public final Object getThis() {
		return this.target;
	}

	@Override
	public AccessibleObject getStaticPart() {
		return null;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final Object[] getArguments() {
		return this.arguments;
	}

	public void setArguments(Object... arguments) {
		this.arguments = arguments;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	public Object proceed() throws Throwable {
		if (this.currentInterceptorIndex == this.interceptors.size() - 1) {
			return method.invoke(target, arguments);
		}else{
			Object interceptor = this.interceptors.get(++this.currentInterceptorIndex);
			return ((MethodInterceptor)interceptor).invoke(this);
		}
	}

}

