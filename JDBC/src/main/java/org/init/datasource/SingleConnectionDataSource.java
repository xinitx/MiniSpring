package org.init.datasource;

import org.init.beans.factory.DisposableBean;
import org.init.core.lang.Nullable;
import org.init.core.util.ObjectUtils;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class SingleConnectionDataSource extends DriverManagerDataSource implements SmartDataSource, DisposableBean {
    private boolean suppressClose;
    @Nullable
    private Boolean autoCommit;
    @Nullable
    private Connection target;
    @Nullable
    private Connection connection;
    private final Object connectionMonitor = new Object();
    public SingleConnectionDataSource() {
    }

    public SingleConnectionDataSource(String url, String username, String password, boolean suppressClose) {
        super(url, username, password);
        this.suppressClose = suppressClose;
    }

    public SingleConnectionDataSource(String url, boolean suppressClose) {
        super(url);
        this.suppressClose = suppressClose;
    }

    public SingleConnectionDataSource(Connection target, boolean suppressClose) {
        if(target == null){
            throw new IllegalArgumentException("Connection must not be null");
        }
        this.target = target;
        this.suppressClose = suppressClose;
        this.connection = suppressClose ? this.getCloseSuppressingConnectionProxy(target) : target;
    }
    public Connection getConnection() throws SQLException {
        synchronized(this.connectionMonitor) {
            if (this.connection == null) {
                this.initConnection();
            }

            if (this.connection.isClosed()) {
                throw new SQLException("Connection was closed in SingleConnectionDataSource. Check that user code checks shouldClose() before closing Connections, or set 'suppressClose' to 'true'");
            } else {
                return this.connection;
            }
        }
    }
    public Connection getConnection(String username, String password) throws SQLException {
        if (ObjectUtils.nullSafeEquals(username, this.getUsername()) && ObjectUtils.nullSafeEquals(password, this.getPassword())) {
            return this.getConnection();
        } else {
            throw new SQLException("SingleConnectionDataSource does not support custom username and password");
        }
    }
    @Override
    public void destroy() {
        synchronized(this.connectionMonitor) {
            this.closeConnection();
        }
    }

    @Override
    public boolean shouldClose(Connection con) {
        synchronized(this.connectionMonitor) {
            return con != this.connection && con != this.target;
        }
    }

    public void initConnection() throws SQLException {
        if (this.getUrl() == null) {
            throw new IllegalStateException("'url' property is required for lazily initializing a Connection");
        } else {
            synchronized(this.connectionMonitor) {
                this.closeConnection();
                this.target = this.getConnectionFromDriver(this.getUsername(), this.getPassword());
                this.prepareConnection(this.target);
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Established shared JDBC Connection: " + this.target);
                }

                this.connection = this.isSuppressClose() ? this.getCloseSuppressingConnectionProxy(this.target) : this.target;
            }
        }
    }
    public void resetConnection() {
        synchronized(this.connectionMonitor) {
            this.closeConnection();
            this.target = null;
            this.connection = null;
        }
    }
    protected void prepareConnection(Connection con) throws SQLException {
        Boolean autoCommit = this.getAutoCommitValue();
        if (autoCommit != null && con.getAutoCommit() != autoCommit) {
            con.setAutoCommit(autoCommit);
        }

    }
    private void closeConnection() {
        if (this.target != null) {
            try {
                this.target.close();
            } catch (Throwable var2) {
                this.logger.warn("Could not close shared JDBC Connection", var2);
            }
        }

    }
    protected Connection getCloseSuppressingConnectionProxy(Connection target) {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, new CloseSuppressingInvocationHandler(target));
    }
    public void setSuppressClose(boolean suppressClose) {
        this.suppressClose = suppressClose;
    }

    protected boolean isSuppressClose() {
        return this.suppressClose;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    @Nullable
    protected Boolean getAutoCommitValue() {
        return this.autoCommit;
    }
    private static class CloseSuppressingInvocationHandler implements InvocationHandler {
        private final Connection target;

        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }

        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("equals")) {
                return proxy == args[0];
            } else if (method.getName().equals("hashCode")) {
                return System.identityHashCode(proxy);
            } else {
                if (method.getName().equals("unwrap")) {
                    if (((Class)args[0]).isInstance(proxy)) {
                        return proxy;
                    }
                } else if (method.getName().equals("isWrapperFor")) {
                    if (((Class)args[0]).isInstance(proxy)) {
                        return true;
                    }
                } else {
                    if (method.getName().equals("close")) {
                        return null;
                    }

                    if (method.getName().equals("isClosed")) {
                        return false;
                    }

                    if (method.getName().equals("getTargetConnection")) {
                        return this.target;
                    }
                }

                try {
                    return method.invoke(this.target, args);
                } catch (InvocationTargetException var5) {
                    throw var5.getTargetException();
                }
            }
        }
    }

}
