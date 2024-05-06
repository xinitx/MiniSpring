package org.init.beans.validation;

import org.init.beans.BeanWrapper;
import org.init.beans.BeanWrapperImpl;
import org.init.beans.ConfigurablePropertyAccessor;

public class BeanPropertyBindingResult extends AbstractPropertyBindingResult{
    private transient BeanWrapper beanWrapper;
    private final Object target;
    public BeanPropertyBindingResult(Object target) {
        this.target = target;
    }
    public final ConfigurablePropertyAccessor getPropertyAccessor() {
        if(this.beanWrapper == null){
            this.beanWrapper = new BeanWrapperImpl(this.target);
        }
        return this.beanWrapper;
    }

    public final Object getTarget() {
        return this.target;
    }
}
