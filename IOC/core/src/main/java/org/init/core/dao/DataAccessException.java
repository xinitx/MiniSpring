package org.init.core.dao;

import org.init.core.NestedRuntimeException;
import org.init.core.lang.Nullable;

public abstract class DataAccessException extends NestedRuntimeException {
    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
