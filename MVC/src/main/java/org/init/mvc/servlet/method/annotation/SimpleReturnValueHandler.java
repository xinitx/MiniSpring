package org.init.mvc.servlet.method.annotation;

import org.init.core.http.converter.DefaultHttpMessageConverter;
import org.init.core.http.converter.HttpMessageConverter;
import org.init.mvc.bind.annotation.ResponseBody;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;
import org.init.mvc.servlet.method.support.HandlerMethodReturnValueHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SimpleReturnValueHandler implements HandlerMethodReturnValueHandler {
    HttpMessageConverter messageConverter = new DefaultHttpMessageConverter();

    public SimpleReturnValueHandler() {
    }
    public SimpleReturnValueHandler(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }
    @Override
    public boolean supportsReturnType(Object var1) {
        return true;
    }

    @Override
    public void handleReturnValue(Method method, Object returnValue, ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {
        if (method.isAnnotationPresent(ResponseBody.class)){ //ResponseBody
            try {
                this.messageConverter.write(returnValue, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (method.getReturnType() == void.class) {

        }
        else {
            if (returnValue instanceof ModelAndView) {
                mav = (ModelAndView)returnValue;
            }
            else if(returnValue instanceof String) {
                String sTarget = (String)returnValue;
                mav = new ModelAndView();
                mav.setViewName(sTarget);
            }
        }
    }
}
