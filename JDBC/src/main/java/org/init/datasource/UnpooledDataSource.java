package org.init.datasource;

import org.init.core.util.ObjectUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class UnpooledDataSource extends DriverManagerDataSource{
    private Boolean autoCommit;
    private Integer defaultTransactionIsolationLevel;
    private final Object connectionMonitor = new Object();
    private Properties driverProperties;
    public UnpooledDataSource() {

    }

    public UnpooledDataSource(String url) {
        super(url);
    }
    public UnpooledDataSource(String url, String username, String password) {
        super(url, username, password);
    }
    public UnpooledDataSource(String url, Properties conProps) {
        super(url, conProps);
    }
    public Connection getConnection() throws SQLException {
        return this.doGetConnection(this.getUsername(), this.getPassword());
    }
    public Connection getConnection(String username, String password) throws SQLException {
        return this.doGetConnection(username, password);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (this.driverProperties != null) {
            props.putAll(this.driverProperties);
        }

        if (username != null) {
            props.setProperty("user", username);
        }

        if (password != null) {
            props.setProperty("password", password);
        }

        return this.doGetConnection(props);
    }
    private Connection doGetConnection(Properties properties) throws SQLException {
        Connection connection = this.getConnectionFromDriver(properties);
        this.configureConnection(connection);
        return connection;
    }

    private void configureConnection(Connection conn) throws SQLException {
        if (this.autoCommit != null && this.autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(this.autoCommit);
        }

        if (this.defaultTransactionIsolationLevel != null) {
            conn.setTransactionIsolation(this.defaultTransactionIsolationLevel);
        }

    }
}
