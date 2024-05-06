package org.init.core;

import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCallback<T> {
    @Nullable
    T doInPreparedStatement(PreparedStatement var1) throws SQLException, DataAccessException;
}