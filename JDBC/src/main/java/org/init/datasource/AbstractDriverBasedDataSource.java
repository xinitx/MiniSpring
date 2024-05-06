package org.init.datasource;

import org.init.core.lang.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public abstract class AbstractDriverBasedDataSource extends AbstractDataSource  {
    @Nullable
    private String url;
    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String catalog;
    @Nullable
    private String schema;
    @Nullable
    private Properties connectionProperties;

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url != null ? url.trim() : null;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @Nullable
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(@Nullable String catalog) {
        this.catalog = catalog;
    }

    @Nullable
    public String getSchema() {
        return schema;
    }

    public void setSchema(@Nullable String schema) {
        this.schema = schema;
    }

    @Nullable
    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(@Nullable Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriver(this.getUsername(), this.getPassword());
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnectionFromDriver(username, password);
    }

    protected Connection getConnectionFromDriver(@Nullable String username, @Nullable String password) throws SQLException {
        Properties mergedProps = new Properties();
        Properties connProps = this.getConnectionProperties();
        if (connProps != null) {
            mergedProps.putAll(connProps);
        }

        if (username != null) {
            mergedProps.setProperty("user", username);
        }

        if (password != null) {
            mergedProps.setProperty("password", password);
        }

        Connection con = this.getConnectionFromDriver(mergedProps);
        if (this.catalog != null) {
            con.setCatalog(this.catalog);
        }

        if (this.schema != null) {
            con.setSchema(this.schema);
        }

        return con;
    }

    protected abstract Connection getConnectionFromDriver(Properties var1) throws SQLException;
}
