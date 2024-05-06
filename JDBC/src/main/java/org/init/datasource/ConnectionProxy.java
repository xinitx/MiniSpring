package org.init.datasource;

import java.sql.Connection;

public interface ConnectionProxy extends Connection {
    Connection getTargetConnection();
}
