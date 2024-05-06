package org.init.mvc.servlet;

public interface ViewResolver {
	View resolveViewName(String viewName) throws Exception;
}
