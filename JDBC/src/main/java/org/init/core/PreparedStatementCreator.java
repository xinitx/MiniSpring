package org.init.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@FunctionalInterface
public interface PreparedStatementCreator {
    PreparedStatement createPreparedStatement(Connection var1) throws SQLException;
}
