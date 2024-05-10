package org.init.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource{
    private final PoolState state = new PoolState(this);
    private final UnpooledDataSource dataSource;
    private List<PooledConnection> connections = null;
    protected int poolMaximumActiveConnections = 10;
    protected int poolMaximumIdleConnections = 5;
    protected int poolMaximumCheckoutTime = 20000;
    protected int poolTimeToWait = 20000;
    protected int poolMaximumLocalBadConnectionTolerance = 3;
    private int expectedConnectionTypeCode;
    protected boolean poolPingEnabled;
    protected int poolPingConnectionsNotUsedFor;
    protected String poolPingQuery = "NO PING QUERY SET";
    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }
    public PooledDataSource(UnpooledDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void forceCloseAll() {
        synchronized(this.state) {
            this.expectedConnectionTypeCode = this.assembleConnectionTypeCode(this.dataSource.getUrl(), this.dataSource.getUsername(), this.dataSource.getPassword());

            int i;
            PooledConnection conn;
            Connection realConn;
            for(i = this.state.activeConnections.size(); i > 0; --i) {
                try {
                    conn = (PooledConnection)this.state.activeConnections.remove(i - 1);
                    conn.invalidate();
                    realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }

                    realConn.close();
                } catch (Exception var7) {
                }
            }

            for(i = this.state.idleConnections.size(); i > 0; --i) {
                try {
                    conn = (PooledConnection)this.state.idleConnections.remove(i - 1);
                    conn.invalidate();
                    realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }

                    realConn.close();
                } catch (Exception var6) {
                }
            }
        }
    }
    protected void pushConnection(PooledConnection conn) throws SQLException {
        synchronized(this.state) {
            this.state.activeConnections.remove(conn);
            if (conn.isValid()) {
                PoolState var10000;
                if (this.state.idleConnections.size() < this.poolMaximumIdleConnections && conn.getConnectionTypeCode() == this.expectedConnectionTypeCode) {
                    var10000 = this.state;
                    var10000.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }

                    PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
                    this.state.idleConnections.add(newConn);
                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                    conn.invalidate();

                    this.state.notifyAll();
                } else {
                    var10000 = this.state;
                    var10000.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }

                    conn.getRealConnection().close();

                    conn.invalidate();
                }
            } else {
                ++this.state.badConnectionCount;
            }

        }
    }
    private PooledConnection popConnection(String username, String password) throws SQLException {
        PooledConnection conn = null;
        boolean countedWait = false;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;
        while(conn == null) {
            synchronized(this.state) {
                PoolState var;
                if (!this.state.idleConnections.isEmpty()) {
                    conn = (PooledConnection)this.state.idleConnections.remove(0);
                }else if (this.state.activeConnections.size() < this.poolMaximumActiveConnections) {
                    conn = new PooledConnection(this.dataSource.getConnection(), this);
                }else {
                    PooledConnection oldestActiveConnection = (PooledConnection)this.state.activeConnections.get(0);
                    long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                    if (longestCheckoutTime > (long)this.poolMaximumCheckoutTime && this.poolMaximumCheckoutTime > 0) {
                        ++this.state.claimedOverdueConnectionCount;
                        var = this.state;
                        var.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                        var = this.state;
                        var.accumulatedCheckoutTime += longestCheckoutTime;
                        this.state.activeConnections.remove(oldestActiveConnection);
                        if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                            try {
                                oldestActiveConnection.getRealConnection().rollback();
                            } catch (SQLException var16) {
                                System.out.println("Bad connection. Could not roll back");
                            }
                        }
                        conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                        conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                        conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                        oldestActiveConnection.invalidate();
                    }else {
                        try {
                            if (!countedWait) {
                                ++this.state.hadToWaitCount;
                                countedWait = true;
                            }

                            long wt = System.currentTimeMillis();
                            this.state.wait((long)this.poolTimeToWait);
                            var = this.state;
                            var.accumulatedWaitTime += System.currentTimeMillis() - wt;
                        } catch (InterruptedException var17) {
                            break;
                        }
                    }
                }

                if (conn != null) {
                    if (conn.isValid()) {
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        conn.setConnectionTypeCode(this.assembleConnectionTypeCode(this.dataSource.getUrl(), username, password));
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        this.state.activeConnections.add(conn);
                        ++this.state.requestCount;
                        var = this.state;
                        var.accumulatedRequestTime += System.currentTimeMillis() - t;
                    }else {
                        ++this.state.badConnectionCount;
                        ++localBadConnectionCount;
                        conn = null;
                        if (localBadConnectionCount > this.poolMaximumIdleConnections + this.poolMaximumLocalBadConnectionTolerance) {
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }

            }
        }
        if (conn == null) {
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        } else {
            return conn;
        }
    }
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;
        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException var8) {
            result = false;
        }

        if (result && this.poolPingEnabled && this.poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > (long)this.poolPingConnectionsNotUsedFor) {
            try {
                Connection realConn = conn.getRealConnection();
                Statement statement = realConn.createStatement();
                ResultSet rs = statement.executeQuery(this.poolPingQuery);
                rs.close();
                statement.close();
                if (!realConn.getAutoCommit()) {
                    realConn.rollback();
                }
                result = true;
            } catch (Exception var7) {

                try {
                    conn.getRealConnection().close();
                } catch (Exception var6) {}
                result = false;
            }
        }

        return result;
    }
    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }
    public Connection getConnection() throws SQLException {
        return this.popConnection(this.dataSource.getUsername(), this.dataSource.getPassword()).getProxyConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.popConnection(username, password).getProxyConnection();
    }

    public void setUrl(String url) {
        this.dataSource.setUrl(url);
        this.forceCloseAll();
    }

    public void setUsername(String username) {
        this.dataSource.setUsername(username);
        this.forceCloseAll();
    }

    public void setPassword(String password) {
        this.dataSource.setPassword(password);
        this.forceCloseAll();
    }
    public String getDriver() {
        return this.dataSource.getDriver();
    }
    public String getUrl() {
        return this.dataSource.getUrl();
    }

    public String getUsername() {
        return this.dataSource.getUsername();
    }

    public String getPassword() {
        return this.dataSource.getPassword();
    }


    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(this.getClass().getName() + " is not a wrapper.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public Logger getParentLogger() {
        return Logger.getLogger("global");
    }
}