package org.init.mvc.context;

import org.init.core.env.ConfigurableEnvironment;
import org.init.core.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public interface ConfigurableWebEnvironment extends ConfigurableEnvironment {
    void initPropertySources(@Nullable ServletContext var1, @Nullable ServletConfig var2);
}