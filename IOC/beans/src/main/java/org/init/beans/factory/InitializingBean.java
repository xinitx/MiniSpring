package org.init.beans.factory;

public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
