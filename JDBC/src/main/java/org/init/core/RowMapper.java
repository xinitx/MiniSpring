package org.init.core;

import org.init.core.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
    @Nullable
    T mapRow(ResultSet var1, int var2) throws SQLException;
}
