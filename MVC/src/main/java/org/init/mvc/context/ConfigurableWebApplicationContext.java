package org.init.mvc.context;

import org.init.context.ConfigurableApplicationContext;
import org.init.core.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {
    String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";
    String SERVLET_CONFIG_BEAN_NAME = "servletConfig";

    void setServletContext(@Nullable ServletContext var1);

    void setServletConfig(@Nullable ServletConfig var1);

    @Nullable
    ServletConfig getServletConfig();

    void setNamespace(@Nullable String var1);

    @Nullable
    String getNamespace();

    void setConfigLocation(String var1);

    void setConfigLocations(String... var1);

    @Nullable
    String[] getConfigLocations();
}
