package org.init.mvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
}
