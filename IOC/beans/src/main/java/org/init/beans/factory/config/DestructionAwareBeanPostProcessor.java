package org.init.beans.factory.config;

import org.init.beans.BeansException;
import org.init.beans.factory.BeanPostProcessor;

public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
    void postProcessBeforeDestruction(Object var1, String var2) throws BeansException;

    default boolean requiresDestruction(Object bean) {
        return true;
    }
}
