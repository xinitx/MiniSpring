package org.init.aop.framework;

import org.init.beans.factory.BeanClassLoaderAware;
import org.init.core.Ordered;
import org.init.core.util.ClassUtils;

public class ProxyProcessorSupport  extends ProxyConfig implements Ordered, BeanClassLoaderAware, AopInfrastructureBean {
    private int order = Integer.MAX_VALUE;
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean classLoaderConfigured = false;
    public void setOrder(int order) {
        this.order = order;
    }
    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }
    public void setProxyClassLoader(ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }
    protected ClassLoader getProxyClassLoader() {
        return this.proxyClassLoader;
    }

}
