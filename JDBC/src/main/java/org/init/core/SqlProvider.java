package org.init.core;

import org.init.core.lang.Nullable;

public interface SqlProvider {
    @Nullable
    String getSql();
}
