package org.init.mvc.context.support;

import org.init.core.env.MutablePropertySources;
import org.init.mvc.context.ConfigurableWebEnvironment;
import org.init.core.env.PropertySource;
import org.init.core.env.StandardEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.List;

public class StandardServletEnvironment extends StandardEnvironment implements ConfigurableWebEnvironment {
    public StandardServletEnvironment() {
    }
    @Override
    public void initPropertySources(ServletContext var1, ServletConfig var2) {

    }
    protected void customizePropertySources(MutablePropertySources propertySources) {
        super.customizePropertySources(propertySources);
    }
}
