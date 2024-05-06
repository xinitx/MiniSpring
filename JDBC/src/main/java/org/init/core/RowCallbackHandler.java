package org.init.core;

import java.sql.ResultSet;
import java.sql.SQLException;
@FunctionalInterface
public interface RowCallbackHandler {
    void processRow(ResultSet var1) throws SQLException;
}
