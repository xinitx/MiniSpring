package org.init.beans.factory.support;

import org.init.beans.BeansException;

@FunctionalInterface
public interface ObjectFactory <T>{
    T getObject() throws BeansException, ClassNotFoundException;
}
