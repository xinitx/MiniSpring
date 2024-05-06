package org.init.mvc.context.support;

import org.init.core.lang.Nullable;
import org.init.mvc.bind.support.WebDataBinderFactory;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;
import org.init.mvc.servlet.method.HandlerMethod;
import org.init.mvc.servlet.method.support.HandlerMethodArgumentResolverComposite;
import org.init.mvc.servlet.method.support.HandlerMethodReturnValueHandlerComposite;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvocableHandlerMethod  extends HandlerMethod {
    @Nullable
    private WebDataBinderFactory dataBinderFactory;
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
    private HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();

    public InvocableHandlerMethod(Method method, Object obj, Class<?> clz, String methodName) {
        super(method, obj, clz, methodName);
    }
    public InvocableHandlerMethod(HandlerMethod method) {
        super(method.getMethod(), method.getBean(), method.getBeanType(), method.getMethodName());
    }
    @Nullable
    public WebDataBinderFactory getDataBinderFactory() {
        return dataBinderFactory;
    }

    public void setDataBinderFactory(@Nullable WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }
    public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.returnValueHandlers = returnValueHandlers;
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }
    @Nullable
    public Object invokeForRequest(HttpServletRequest request, HttpServletResponse response, ModelAndView mavContainer, Object... providedArgs) throws Exception {
        Object[] args = this.getMethodArgumentValues(request, response, mavContainer, providedArgs);
        Object returnValue = this.getMethod().invoke(this.getBean(), args);
        return returnValue;
    }

    private Object[] getMethodArgumentValues(HttpServletRequest request, HttpServletResponse response, ModelAndView mavContainer, Object[] providedArgs) {
        Parameter[] methodParameters = this.getMethod().getParameters();
        Object[] args = new Object[methodParameters.length];
        for(int i = 0; i < methodParameters.length; ++i) {
            if (args[i] == null) {
                args[i] = this.argumentResolvers.resolveArgument(methodParameters[i], mavContainer, request, response,  this.dataBinderFactory);
            }
        }
        return args;
    }

    public void invokeAndHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mav, Object[] objects) {
        Object returnValue = null;
        try {
            returnValue = this.invokeForRequest(request, response, mav, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.returnValueHandlers.handleReturnValue(this.getMethod(),returnValue, mav, request, response);
    }

}
