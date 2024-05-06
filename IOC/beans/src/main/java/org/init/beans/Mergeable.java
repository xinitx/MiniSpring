package org.init.beans;

import org.init.core.lang.Nullable;

public interface Mergeable {
    boolean isMergeEnabled();

    Object merge(@Nullable Object var1);
}
