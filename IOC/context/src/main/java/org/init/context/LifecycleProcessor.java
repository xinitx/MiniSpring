package org.init.context;

public interface LifecycleProcessor extends Lifecycle {
    void onRefresh();

    void onClose();
}
