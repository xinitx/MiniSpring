package org.init.core;

import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
@FunctionalInterface
public interface ResultSetExtractor<T> {
    @Nullable
    T extractData(ResultSet var1) throws SQLException, DataAccessException;
}
