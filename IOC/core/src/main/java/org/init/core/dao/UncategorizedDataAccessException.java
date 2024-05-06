package org.init.core.dao;

import org.init.core.lang.Nullable;

public abstract class UncategorizedDataAccessException extends NonTransientDataAccessException  {
    public UncategorizedDataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
