package org.init.context;

import org.init.beans.factory.Aware;
import org.init.core.env.Environment;

public interface EnvironmentAware extends Aware {
    void setEnvironment(Environment var1);
}
