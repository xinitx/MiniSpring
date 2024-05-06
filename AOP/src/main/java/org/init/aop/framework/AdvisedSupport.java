package org.init.aop.framework;

import org.init.aop.Advisor;
import org.init.core.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;

public class AdvisedSupport extends ProxyConfig implements Advised  {

    TargetSource targetSource;
    private List<Advisor> advisors = new LinkedList();
    private List<Class<?>> interfaces = new ArrayList();
    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();
    public void setTargetSource( TargetSource targetSource) {
        this.targetSource = targetSource != null ? targetSource : null;
    }
    public TargetSource getTargetSource() {
        return this.targetSource;
    }
    public void addAdvisor(Advisor advisor) {
        advisors.add(advisor);
    }
    public void addAdvisors(Collection<Advisor> advisors) {
        if (advisors != null && !advisors.isEmpty()) {
            Iterator var2 = advisors.iterator();

            while(var2.hasNext()) {
                Advisor advisor = (Advisor)var2.next();
                this.advisors.add(advisor);
            }
        }
    }
    public void addInterface(Class ifc) {
        if (!this.interfaces.contains(ifc)) {
            this.interfaces.add(ifc);
        }
    }
    public Class<?>[] getProxiedInterfaces() {
        return ClassUtils.toClassArray(this.interfaces);
    }
    @Override
    public Class<?> getTargetClass() {
        return this.targetSource.getTargetClass();
    }

    public List<Object> getInterceptors(Method method, Class<?> targetClass) {
        List<Object> cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(this, method, targetClass);
        return cached;
    }

    public List<Advisor> getAdvisors() {
        return this.advisors;
    }
}
