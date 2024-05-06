package org.init.mvc.context;

import org.init.beans.factory.Aware;

import javax.servlet.ServletContext;

public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext var1);
}