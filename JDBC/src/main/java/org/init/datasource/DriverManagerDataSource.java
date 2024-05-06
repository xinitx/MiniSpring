package org.init.datasource;

import org.init.core.util.ClassUtils;
import org.init.core.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DriverManagerDataSource extends AbstractDriverBasedDataSource  {
    public DriverManagerDataSource() {
    }

    public DriverManagerDataSource(String url) {
        this.setUrl(url);
    }

    public DriverManagerDataSource(String url, String username, String password) {
        this.setUrl(url);
        this.setUsername(username);
        this.setPassword(password);
    }
    public DriverManagerDataSource(String url, Properties conProps) {
        this.setUrl(url);
        this.setConnectionProperties(conProps);
    }
    public void setDriverClassName(String driverClassName) {
        if(!StringUtils.hasText(driverClassName)) {
            throw new IllegalStateException("Property 'driverClassName' must not be empty");
        }
        String driverClassNameToUse = driverClassName.trim();

        try {
            Class.forName(driverClassNameToUse, true, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException var4) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassNameToUse + "]", var4);
        }

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Loaded JDBC driver: " + driverClassNameToUse);
        }

    }
    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
        String url = this.getUrl();
        if(url == null){
            throw new IllegalStateException("'url' not set");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating new JDBC DriverManager Connection to [" + url + "]");
        }

        return this.getConnectionFromDriverManager(url, props);
    }

    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }

}
