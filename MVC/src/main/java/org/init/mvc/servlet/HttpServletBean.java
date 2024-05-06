package org.init.mvc.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.context.EnvironmentAware;
import org.init.mvc.context.support.StandardServletEnvironment;
import org.init.core.env.ConfigurableEnvironment;
import org.init.core.env.Environment;
import org.init.core.env.EnvironmentCapable;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.*;

public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ConfigurableEnvironment environment;
    private final Set<String> requiredProperties = new HashSet(4);

    public HttpServletBean() {
    }
    public final void init() throws ServletException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Initializing servlet '" + this.getServletName() + "'");
        }

        this.initServletBean();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Servlet '" + this.getServletName() + "' configured successfully");
        }
    }
    protected void initServletBean() throws ServletException {
    }
    protected final void addRequiredProperty(String property) {
        this.requiredProperties.add(property);
    }
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment, "ConfigurableEnvironment required");
        this.environment = (ConfigurableEnvironment)environment;
    }
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }

        return this.environment;
    }
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }



}
