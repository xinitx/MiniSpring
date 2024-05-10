package org.init.context;

import org.init.beans.BeanFactory;
import org.init.core.lang.Nullable;

public interface HierarchicalBeanFactory {
    @Nullable
    BeanFactory getParentBeanFactory();

    boolean containsLocalBean(String name);
}
