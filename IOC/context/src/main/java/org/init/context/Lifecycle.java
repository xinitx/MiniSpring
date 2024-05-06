package org.init.context;

public interface Lifecycle {
    void start();

    void stop();

    boolean isRunning();
}
