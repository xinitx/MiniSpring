package org.init.mvc.servlet.method.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.core.lang.Nullable;
import org.init.mvc.bind.support.WebDataBinderFactory;
import org.init.mvc.servlet.MethodParameter;
import org.init.mvc.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver  {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final List<HandlerMethodArgumentResolver> argumentResolvers = new LinkedList();
    public HandlerMethodArgumentResolverComposite addResolver(HandlerMethodArgumentResolver resolver) {
        this.argumentResolvers.add(resolver);
        return this;
    }
    public boolean supportsParameter(Parameter parameter) {
        return this.getArgumentResolver(parameter) != null;
    }

    @Override
    public Object resolveArgument(Parameter methodParameter, ModelAndView mavContainer, HttpServletRequest request, HttpServletResponse response, WebDataBinderFactory dataBinderFactory) {
        HandlerMethodArgumentResolver resolver = this.getArgumentResolver(methodParameter);
        if (resolver == null) {
            throw new IllegalArgumentException("Unknown parameter type [" + methodParameter.getName() + "]");
        } else {
            return resolver.resolveArgument(methodParameter, mavContainer, request, response, dataBinderFactory);
        }
    }
    public HandlerMethodArgumentResolverComposite addResolvers(@Nullable List<? extends HandlerMethodArgumentResolver> resolvers) {
        if (resolvers != null) {
            Iterator var2 = resolvers.iterator();

            while(var2.hasNext()) {
                HandlerMethodArgumentResolver resolver = (HandlerMethodArgumentResolver)var2.next();
                this.argumentResolvers.add(resolver);
            }
        }

        return this;
    }
    public HandlerMethodArgumentResolverComposite addResolvers(@Nullable HandlerMethodArgumentResolver... resolvers) {
        if (resolvers != null) {
            HandlerMethodArgumentResolver[] var2 = resolvers;
            int var3 = resolvers.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                HandlerMethodArgumentResolver resolver = var2[var4];
                this.argumentResolvers.add(resolver);
            }
        }

        return this;
    }
    @Nullable
    private HandlerMethodArgumentResolver getArgumentResolver(Parameter parameter) {
            HandlerMethodArgumentResolver result = null;
            Iterator var = this.argumentResolvers.iterator();
            while(var.hasNext()) {
                HandlerMethodArgumentResolver methodArgumentResolver = (HandlerMethodArgumentResolver)var.next();
                if (methodArgumentResolver.supportsParameter(parameter)) {
                    result = methodArgumentResolver;
                    break;
                }
            }
        return result;
    }


}
