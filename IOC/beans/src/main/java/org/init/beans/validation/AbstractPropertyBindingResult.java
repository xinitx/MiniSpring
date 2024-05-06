package org.init.beans.validation;

import org.init.beans.ConfigurablePropertyAccessor;
import org.init.beans.PropertyEditorRegistry;

public abstract class AbstractPropertyBindingResult {

    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.getTarget() != null ? this.getPropertyAccessor() : null;
    }
    public abstract Object getTarget();
    public abstract ConfigurablePropertyAccessor getPropertyAccessor();
}
