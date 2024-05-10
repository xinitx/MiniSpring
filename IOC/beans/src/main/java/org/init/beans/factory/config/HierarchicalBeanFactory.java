package org.init.beans.factory.config;

import org.init.beans.BeanFactory;
import org.init.core.lang.Nullable;

public interface HierarchicalBeanFactory extends BeanFactory {
    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String name);
}