package org.init.mvc.bind.support;

import org.init.core.lang.Nullable;
import org.init.mvc.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultDataBinderFactory implements WebDataBinderFactory  {
    @Nullable
    private final WebBindingInitializer initializer;
    public DefaultDataBinderFactory(@Nullable WebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public WebDataBinder createBinder(HttpServletRequest request, HttpServletResponse response, Object target, String objectName) throws Exception {
        WebDataBinder dataBinder = this.createBinderInstance(target, objectName, request, response);
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder);
        }
        this.initBinder(dataBinder, request, response);
        return dataBinder;
    }

    protected void initBinder(WebDataBinder dataBinder, HttpServletRequest request, HttpServletResponse response) {

    }

    protected WebDataBinder createBinderInstance(@Nullable Object target, String objectName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new WebDataBinder(target, objectName);
    }
}
