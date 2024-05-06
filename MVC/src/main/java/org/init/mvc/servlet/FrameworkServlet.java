package org.init.mvc.servlet;

import org.init.beans.BeansException;
import org.init.context.*;
import org.init.context.event.ContextRefreshedEvent;
import org.init.mvc.context.ConfigurableWebApplicationContext;
import org.init.mvc.context.WebApplicationContext;
import org.init.mvc.context.support.XmlWebApplicationContext;
import org.init.core.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class FrameworkServlet extends HttpServletBean implements ApplicationContextAware {


    @Nullable
    private WebApplicationContext webApplicationContext;
    @Nullable
    private String contextAttribute;
    private Class<?> contextClass = XmlWebApplicationContext.class;
    @Nullable
    private String contextId;
    private boolean webApplicationContextInjected;
    @Nullable
    private String contextConfigLocation;
    private boolean refreshEventReceived;
    public FrameworkServlet() {
        this.webApplicationContextInjected = false;
    }
    protected final void initServletBean() throws ServletException {
        this.getServletContext().log("Initializing Spring FrameworkServlet '" + this.getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("FrameworkServlet '" + this.getServletName() + "': initialization started");
        }
        ServletConfig config = this.getServletConfig();
        contextConfigLocation = config.getInitParameter("contextConfigLocation");
        long startTime = System.currentTimeMillis();

        try {
            this.webApplicationContext = this.initWebApplicationContext();
            this.initFrameworkServlet();
        } catch (RuntimeException var5) {
            this.logger.error("Context initialization failed", var5);
            throw var5;
        }

        if (this.logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this.logger.info("FrameworkServlet '" + this.getServletName() + "': initialization completed in " + elapsedTime + " ms");
        }

    }
    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext rootContext = (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        WebApplicationContext wac = null;
        if (this.webApplicationContext != null) {
            wac = this.webApplicationContext;
            if (wac instanceof ConfigurableWebApplicationContext) {
                ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext)wac;
                if (!cwac.isActive()) {
                    if (cwac.getParent() == null) {
                        cwac.setParent(rootContext);
                    }
                    this.configureAndRefreshWebApplicationContext(cwac);
                }
            }
        }
        if (wac == null) {
            wac = this.findWebApplicationContext();
        }
        if (wac == null) {
            wac = this.createWebApplicationContext(rootContext);
        }
        if (!this.refreshEventReceived) {
            this.onRefresh(wac);
        }
        return wac;
    }
    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac){
        wac.setServletContext(this.getServletContext());
        wac.setServletConfig(this.getServletConfig());
        try {
            wac.refresh();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }
    @Nullable
    protected WebApplicationContext findWebApplicationContext() {
        String attrName = this.getContextAttribute();
        if (attrName == null) {
            return null;
        } else {
            WebApplicationContext wac = (WebApplicationContext) this.getServletContext().getAttribute(attrName);
            if (wac == null) {
                throw new IllegalStateException("No WebApplicationContext found: initializer not registered?");
            } else {
                return wac;
            }
        }
    }
    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        Class<?> contextClass = this.getContextClass();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Servlet with name '" + this.getServletName() + "' will try to create custom WebApplicationContext context of class '" + contextClass.getName() + "', using parent context [" + parent + "]");
        }

        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new RuntimeException("Fatal initialization error in servlet with name '" + this.getServletName() + "': custom WebApplicationContext class [" + contextClass.getName() + "] is not of type ConfigurableWebApplicationContext");
        } else {
            ConfigurableWebApplicationContext wac = null;
            try {
                Constructor ctor = contextClass.getDeclaredConstructor();
                wac = (ConfigurableWebApplicationContext)ctor.newInstance();
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            wac.setEnvironment(this.getEnvironment());
            wac.setParent(parent);
            String configLocation = this.getContextConfigLocation();
            if (configLocation != null) {
                wac.setConfigLocation(configLocation);
            }
            this.configureAndRefreshWebApplicationContext(wac);
            return wac;
        }
    }
    protected void initFrameworkServlet() throws ServletException {
    }
    public FrameworkServlet(WebApplicationContext webApplicationContext) {
        this.webApplicationContextInjected = false;
        this.webApplicationContext = webApplicationContext;
    }
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (this.webApplicationContext == null && applicationContext instanceof WebApplicationContext) {
            this.webApplicationContext = (WebApplicationContext)applicationContext;
            this.webApplicationContextInjected = true;
        }

    }

    @Nullable
    public WebApplicationContext getWebApplicationContext() {
        return webApplicationContext;
    }

    public void setWebApplicationContext(@Nullable WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    public void setContextAttribute(@Nullable String contextAttribute) {
        this.contextAttribute = contextAttribute;
    }

    public void setContextClass(Class<?> contextClass) {
        this.contextClass = contextClass;
    }

    @Nullable
    public String getContextId() {
        return contextId;
    }

    public void setContextId(@Nullable String contextId) {
        this.contextId = contextId;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }
    @Nullable
    public String getContextConfigLocation() {
        return contextConfigLocation;
    }
    @Nullable
    public String getContextAttribute() {
        return this.contextAttribute;
    }
    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        this.onRefresh(event.getApplicationContext());
    }
    protected void onRefresh(ApplicationContext context) {
    }
}
