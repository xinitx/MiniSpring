package org.init.mvc.servlet.method.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.core.lang.Nullable;
import org.init.mvc.bind.annotation.ResponseBody;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HandlerMethodReturnValueHandlerComposite {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList();
    public HandlerMethodReturnValueHandlerComposite() {
    }

    public List<HandlerMethodReturnValueHandler> getHandlers() {
        return Collections.unmodifiableList(this.returnValueHandlers);
    }

    public boolean supportsReturnType(Object returnType) {
        return this.getReturnValueHandler(returnType) != null;
    }


    @Nullable
    private HandlerMethodReturnValueHandler getReturnValueHandler(Object returnType) {
        Iterator var2 = this.returnValueHandlers.iterator();

        HandlerMethodReturnValueHandler handler;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            handler = (HandlerMethodReturnValueHandler)var2.next();
        } while(!handler.supportsReturnType(returnType));

        return handler;
    }

    public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler handler) {
        this.returnValueHandlers.add(handler);
        return this;
    }

    public HandlerMethodReturnValueHandlerComposite addHandlers(@Nullable List<? extends HandlerMethodReturnValueHandler> handlers) {
        if (handlers != null) {
            this.returnValueHandlers.addAll(handlers);
        }
        return this;
    }


    public void handleReturnValue(Method method, Object returnValue, ModelAndView mav, HttpServletRequest request, HttpServletResponse response) {
        for (HandlerMethodReturnValueHandler handler : this.returnValueHandlers){
            if (handler.supportsReturnType(returnValue)){
                handler.handleReturnValue(method, returnValue, mav, request, response);
                return;
            }
        }
    }
}
