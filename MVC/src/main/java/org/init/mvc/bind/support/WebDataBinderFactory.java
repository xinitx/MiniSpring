package org.init.mvc.bind.support;

import org.init.core.lang.Nullable;
import org.init.mvc.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebDataBinderFactory {
    WebDataBinder createBinder(HttpServletRequest request, HttpServletResponse response, @Nullable Object target, String objectName) throws Exception;
}
