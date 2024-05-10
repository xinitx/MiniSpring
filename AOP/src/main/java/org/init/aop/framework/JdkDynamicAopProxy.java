package org.init.aop.framework;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.init.core.util.ClassUtils;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {

    private final AdvisedSupport advised;
    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }
    @Override
    public Object getProxy() {
        return this.getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Class<?>[] proxiedInterfaces = this.advised.getProxiedInterfaces();
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Integer var_hashcode;
        if(method.getName().equals("equals")){
            Class<?>[] paramTypes = method.getParameterTypes();
            if(paramTypes.length == 1 && paramTypes[0] == Object.class){
                Boolean var = this.equals(args[0]);
                return var;
            }
        }
        Object retVal;
        TargetSource targetSource = this.advised.targetSource;
        Object target = targetSource.getTarget();
        Class<?> targetClass = target != null ? target.getClass() : null;
        List<Object> chain = this.advised.getInterceptors(method, targetClass);
        MethodInvocation mi = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
        retVal = mi.proceed();
        return retVal;
    }
}
