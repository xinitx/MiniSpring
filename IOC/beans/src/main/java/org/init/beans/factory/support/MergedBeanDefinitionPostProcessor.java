package org.init.beans.factory.support;

import org.init.beans.BeanDefinition;
import org.init.beans.factory.BeanPostProcessor;

public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
    void postProcessMergedBeanDefinition(BeanDefinition var1, Class<?> var2, String var3);
}
