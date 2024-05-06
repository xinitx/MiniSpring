package org.init.aop;
@FunctionalInterface
public interface ClassFilter {
    //ClassFilter TRUE = TrueClassFilter.INSTANCE;

    boolean matches(Class<?> var1);
}
