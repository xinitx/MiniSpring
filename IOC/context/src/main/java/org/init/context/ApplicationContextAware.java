package org.init.context;

import org.init.beans.BeansException;
import org.init.beans.factory.Aware;

public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext var1) throws BeansException;
}
