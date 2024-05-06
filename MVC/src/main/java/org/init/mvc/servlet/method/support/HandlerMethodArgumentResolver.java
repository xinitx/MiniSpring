package org.init.mvc.servlet.method.support;

import org.init.core.lang.Nullable;
import org.init.mvc.bind.support.WebDataBinderFactory;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

public interface HandlerMethodArgumentResolver {
    boolean supportsParameter(Parameter var1);


    Object resolveArgument(Parameter methodParameter, ModelAndView mavContainer, HttpServletRequest request, HttpServletResponse response, WebDataBinderFactory dataBinderFactory);
}
