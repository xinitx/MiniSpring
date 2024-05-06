package org.init.mvc.context;

import org.init.beans.factory.Aware;

import javax.servlet.ServletConfig;

public interface ServletConfigAware extends Aware {
    void setServletConfig(ServletConfig var1);
}
