package org.init.mvc.servlet.method.annotation;

import org.init.mvc.bind.WebDataBinder;
import org.init.mvc.bind.annotation.PathVariable;
import org.init.mvc.bind.support.WebDataBinderFactory;
import org.init.mvc.servlet.ModelAndView;
import org.init.mvc.servlet.method.support.HandlerMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

public class SimpleArgumentResolver  implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(Parameter var1) {
        return true;
    }

    @Override
    public Object resolveArgument(Parameter methodParameter, ModelAndView mavContainer, HttpServletRequest request, HttpServletResponse response, WebDataBinderFactory dataBinderFactory) {
        if (methodParameter.getType()==HttpServletRequest.class) {
            return request;
        }
        else if (methodParameter.getType()==HttpServletResponse.class) {
            return response;
        }
        else if (methodParameter.isAnnotationPresent(PathVariable.class)) {
            String sServletPath = request.getServletPath();
            int index = sServletPath.lastIndexOf("/");
            String sParam = sServletPath.substring(index+1);
            if (int.class.isAssignableFrom(methodParameter.getType())) {
                return Integer.parseInt(sParam);
            } else if (String.class.isAssignableFrom(methodParameter.getType())) {
                return sParam;
            }
        }
        else if (methodParameter.getType()!=HttpServletRequest.class && methodParameter.getType()!=HttpServletResponse.class) {
            Object methodParamObj = null;
            try {
                methodParamObj = methodParameter.getType().newInstance();
                WebDataBinder wdb = null;
                try {
                    wdb = dataBinderFactory.createBinder(request, response, methodParamObj, methodParameter.getName());
                    wdb.bind(request, response);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return methodParamObj;
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
