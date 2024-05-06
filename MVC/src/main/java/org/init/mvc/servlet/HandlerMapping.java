package org.init.mvc.servlet;


import org.init.mvc.servlet.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping {
	HandlerMethod getHandler(HttpServletRequest request) throws Exception;
}
