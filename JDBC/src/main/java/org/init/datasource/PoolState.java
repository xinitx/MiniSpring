package org.init.datasource;

import java.util.ArrayList;
import java.util.List;

public class PoolState {
    protected PooledDataSource dataSource;
    protected final List<PooledConnection> idleConnections = new ArrayList();
    protected final List<PooledConnection> activeConnections = new ArrayList();
    protected long requestCount = 0L;
    protected long accumulatedRequestTime = 0L;
    protected long accumulatedCheckoutTime = 0L;
    protected long claimedOverdueConnectionCount = 0L;
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0L;
    protected long accumulatedWaitTime = 0L;
    protected long hadToWaitCount = 0L;
    protected long badConnectionCount = 0L;

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public synchronized long getRequestCount() {
        return this.requestCount;
    }

    public synchronized long getAverageRequestTime() {
        return this.requestCount == 0L ? 0L : this.accumulatedRequestTime / this.requestCount;
    }

    public synchronized long getAverageWaitTime() {
        return this.hadToWaitCount == 0L ? 0L : this.accumulatedWaitTime / this.hadToWaitCount;
    }

    public synchronized long getHadToWaitCount() {
        return this.hadToWaitCount;
    }

    public synchronized long getBadConnectionCount() {
        return this.badConnectionCount;
    }

    public synchronized long getClaimedOverdueConnectionCount() {
        return this.claimedOverdueConnectionCount;
    }

    public synchronized long getAverageOverdueCheckoutTime() {
        return this.claimedOverdueConnectionCount == 0L ? 0L : this.accumulatedCheckoutTimeOfOverdueConnections / this.claimedOverdueConnectionCount;
    }

    public synchronized long getAverageCheckoutTime() {
        return this.requestCount == 0L ? 0L : this.accumulatedCheckoutTime / this.requestCount;
    }

    public synchronized int getIdleConnectionCount() {
        return this.idleConnections.size();
    }

    public synchronized int getActiveConnectionCount() {
        return this.activeConnections.size();
    }

    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n===CONFINGURATION==============================================");
        builder.append("\n jdbcDriver                     ").append(this.dataSource.getDriver());
        builder.append("\n jdbcUrl                        ").append(this.dataSource.getUrl());
        builder.append("\n jdbcUsername                   ").append(this.dataSource.getUsername());
        builder.append("\n jdbcPassword                   ").append(this.dataSource.getPassword() == null ? "NULL" : "************");
        builder.append("\n poolMaxActiveConnections       ").append(this.dataSource.poolMaximumActiveConnections);
        builder.append("\n poolMaxIdleConnections         ").append(this.dataSource.poolMaximumIdleConnections);
        builder.append("\n poolMaxCheckoutTime            ").append(this.dataSource.poolMaximumCheckoutTime);
        builder.append("\n poolTimeToWait                 ").append(this.dataSource.poolTimeToWait);
        builder.append("\n ---STATUS-----------------------------------------------------");
        builder.append("\n activeConnections              ").append(this.getActiveConnectionCount());
        builder.append("\n idleConnections                ").append(this.getIdleConnectionCount());
        builder.append("\n requestCount                   ").append(this.getRequestCount());
        builder.append("\n averageRequestTime             ").append(this.getAverageRequestTime());
        builder.append("\n averageCheckoutTime            ").append(this.getAverageCheckoutTime());
        builder.append("\n claimedOverdue                 ").append(this.getClaimedOverdueConnectionCount());
        builder.append("\n averageOverdueCheckoutTime     ").append(this.getAverageOverdueCheckoutTime());
        builder.append("\n hadToWait                      ").append(this.getHadToWaitCount());
        builder.append("\n averageWaitTime                ").append(this.getAverageWaitTime());
        builder.append("\n badConnectionCount             ").append(this.getBadConnectionCount());
        builder.append("\n===============================================================");
        return builder.toString();
    }
}

