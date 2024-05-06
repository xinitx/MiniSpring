package org.init.mvc.servlet.method.support;

import org.init.core.lang.Nullable;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface HandlerMethodReturnValueHandler {
    boolean supportsReturnType(Object var1);


    void handleReturnValue(Method method, Object returnValue, ModelAndView mav, HttpServletRequest request, HttpServletResponse response);
}
