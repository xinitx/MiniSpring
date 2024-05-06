package org.init.aop.support;

import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLExceptionTranslator {
    @Nullable
    DataAccessException translate(String var1, @Nullable String var2, SQLException var3);
}
