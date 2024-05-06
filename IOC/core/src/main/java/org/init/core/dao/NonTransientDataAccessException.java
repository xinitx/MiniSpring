package org.init.core.dao;

import org.init.core.lang.Nullable;

public abstract class NonTransientDataAccessException extends DataAccessException {
    public NonTransientDataAccessException(String msg) {
        super(msg);
    }

    public NonTransientDataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}