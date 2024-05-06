package org.init.aop.aspectj;

import org.init.aop.ClassFilter;
import org.init.aop.IntroductionAwareMethodMatcher;
import org.init.aop.MethodMatcher;
import org.init.beans.BeanFactory;
import org.init.beans.BeansException;
import org.init.beans.factory.BeanFactoryAware;
import org.init.aop.support.AbstractExpressionPointcut;

import java.lang.reflect.Method;

public class AspectJExpressionPointcut extends AbstractExpressionPointcut implements ClassFilter, IntroductionAwareMethodMatcher, BeanFactoryAware {

    public AspectJExpressionPointcut() {
    }
    @Override
    public boolean matches(Method var1, Class<?> var2, boolean var3) {
        return false;
    }
    @Override
    public boolean matches(Method var1, Class<?> var2) {
        return false;
    }
    @Override
    public boolean isRuntime() {
        return false;
    }

    @Override
    public boolean matches(Method var1, Class<?> var2, Object... var3) {
        return false;
    }

    @Override
    public boolean matches(Class<?> var1) {
        return false;
    }

    @Override
    public void setBeanFactory(BeanFactory var1) throws BeansException {

    }

    @Override
    public ClassFilter getClassFilter() {
        return null;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return null;
    }
}
