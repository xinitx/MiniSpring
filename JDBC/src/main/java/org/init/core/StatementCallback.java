package org.init.core;

import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;

import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
public interface StatementCallback<T> {
    @Nullable
    T doInStatement(Statement var1) throws SQLException, DataAccessException;
}
