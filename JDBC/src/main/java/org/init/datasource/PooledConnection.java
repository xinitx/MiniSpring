package org.init.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class PooledConnection implements InvocationHandler {
	private static final Class<?>[] IFACES = new Class[]{Connection.class};
	private final int hashCode;
	private final Connection realConnection;
	private final Connection proxyConnection;
	private final PooledDataSource dataSource;
	private boolean valid;
	private long checkoutTimestamp;
	private long createdTimestamp;
	private long lastUsedTimestamp;
	private int connectionTypeCode;
	public PooledConnection(Connection connection, PooledDataSource dataSource) {
		this.hashCode = connection.hashCode();
		this.realConnection = connection;
		this.dataSource = dataSource;
		this.valid = true;
		this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
		this.createdTimestamp = System.currentTimeMillis();
		this.lastUsedTimestamp = System.currentTimeMillis();
	}
	public void invalidate() {
		this.valid = false;
	}
	public boolean isValid() {
		return this.valid && this.realConnection != null && this.dataSource.pingConnection(this);
	}
	public Connection getRealConnection() {
		return this.realConnection;
	}

	public Connection getProxyConnection() {
		return this.proxyConnection;
	}
	public int getRealHashCode() {
		return this.realConnection == null ? 0 : this.realConnection.hashCode();
	}
	public int hashCode() {
		return this.hashCode;
	}
	public boolean equals(Object obj) {
		if (obj instanceof PooledConnection) {
			return this.realConnection.hashCode() == ((PooledConnection)obj).realConnection.hashCode();
		} else if (obj instanceof Connection) {
			return this.hashCode == obj.hashCode();
		} else {
			return false;
		}
	}
	public long getCheckoutTime() {
		return System.currentTimeMillis() - this.checkoutTimestamp;
	}
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		if ("close".hashCode() == methodName.hashCode() && "close".equals(methodName)) {
			this.dataSource.pushConnection(this);
			return null;
		} else {
			try {
				if (!Object.class.equals(method.getDeclaringClass())) {
					this.checkConnection();
				}

				return method.invoke(this.realConnection, args);
			} catch (Throwable var6) {
				throw new RuntimeException(var6);
			}
		}
	}
	private void checkConnection() throws SQLException {
		if (!this.valid) {
			throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
		}
	}
	public long getCreatedTimestamp() {
		return this.createdTimestamp;
	}

	public void setCreatedTimestamp(long createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public long getLastUsedTimestamp() {
		return this.lastUsedTimestamp;
	}

	public void setLastUsedTimestamp(long lastUsedTimestamp) {
		this.lastUsedTimestamp = lastUsedTimestamp;
	}
	public long getCheckoutTimestamp() {
		return this.checkoutTimestamp;
	}

	public void setCheckoutTimestamp(long timestamp) {
		this.checkoutTimestamp = timestamp;
	}
	public int getConnectionTypeCode() {
		return this.connectionTypeCode;
	}

	public void setConnectionTypeCode(int connectionTypeCode) {
		this.connectionTypeCode = connectionTypeCode;
	}

	public long getTimeElapsedSinceLastUse() {
		return System.currentTimeMillis() - this.lastUsedTimestamp;
	}
}
