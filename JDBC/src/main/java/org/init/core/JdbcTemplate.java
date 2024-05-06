package org.init.core;

import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;
import org.init.core.util.Assert;
import org.init.aop.support.JdbcAccessor;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTemplate extends JdbcAccessor implements JdbcOperations   {
    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        this.setDataSource(dataSource);
        //this.afterPropertiesSet();
    }

    public JdbcTemplate(DataSource dataSource, boolean lazyInit) {
        this.setDataSource(dataSource);
        this.setLazyInit(lazyInit);
        //this.afterPropertiesSet();
    }
    @Nullable
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        Connection con = null;
        Statement stmt = null;
        try {
            con = this.obtainDataSource().getConnection();
            stmt = con.createStatement();
            return action.doInStatement(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                stmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void execute(final String sql) throws DataAccessException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Executing SQL statement [" + sql + "]");
        }
        this.execute((stmt)->{
            stmt.execute(sql);
            return null;
        });
    }
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        if(sql == null || rse == null){
            throw new RuntimeException("sql or rse is null");
        }
        return this.execute(stmt -> {
            ResultSet rs = null;
            T var;
            try {
                rs = stmt.executeQuery(sql);
                var = rse.extractData(rs);
            } finally {
                rs.close();
            }
            return var;
        });
    }
    //逐行处理数据，没有返回值
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        this.query((String)sql, (ResultSetExtractor)(new RowCallbackHandlerResultSetExtractor(rch)));
    }
    //处理ResultSet并返回
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> result = (List)this.query((String)sql, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper)));
        if (result == null){
            throw new RuntimeException("result is null");
        }
        return result;
    }
    @Nullable
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        PreparedStatement ps = null;
        Connection con = null;
        try {
            con = this.obtainDataSource().getConnection();
            ps = psc.createPreparedStatement(con);
            return action.doInPreparedStatement(ps);
        } catch (SQLException e) {
            try {
                ps.close();
                con.close();
            } catch (SQLException e2) {
                throw new RuntimeException(e2);
            }
            throw new RuntimeException(e);
        }finally {
            try {
                ps.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
    @Nullable
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return (T) this.execute((PreparedStatementCreator)(new SimplePreparedStatementCreator(sql)), (PreparedStatementCallback)action);
    }

    @Nullable
    public <T> T query(PreparedStatementCreator psc, @Nullable final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        Assert.notNull(rse, "ResultSetExtractor must not be null");
        this.logger.debug("Executing prepared SQL query");
        return this.execute(psc, new PreparedStatementCallback<T>() {
            @Nullable
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = null;

                Object var3;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }

                    rs = ps.executeQuery();
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer)pss).cleanupParameters();
                    }

                }
            }
        });
    }

    @Nullable
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        return (T) this.query((PreparedStatementCreator)psc, (PreparedStatementSetter)null, (ResultSetExtractor)rse);
    }

    @Nullable
    public <T> T query(String sql, @Nullable PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        return (T) this.query((PreparedStatementCreator)(new SimplePreparedStatementCreator(sql)), (PreparedStatementSetter)pss, (ResultSetExtractor)rse);
    }
    @Nullable
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, this.newArgTypePreparedStatementSetter(args, argTypes), rse);
    }

    @Nullable
    public <T> T query(String sql, @Nullable Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }

    @Nullable
    public <T> T query(String sql, ResultSetExtractor<T> rse, @Nullable Object... args) throws DataAccessException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }

    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        this.query((PreparedStatementCreator)psc, (ResultSetExtractor)(new RowCallbackHandlerResultSetExtractor(rch)));
    }

    public void query(String sql, @Nullable PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        this.query((String)sql, (PreparedStatementSetter)pss, (ResultSetExtractor)(new RowCallbackHandlerResultSetExtractor(rch)));
    }

    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, this.newArgTypePreparedStatementSetter(args, argTypes), rch);
    }

    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }

    public void query(String sql, RowCallbackHandler rch, @Nullable Object... args) throws DataAccessException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }

    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)result(this.query((PreparedStatementCreator)psc, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper))));
    }

    public <T> List<T> query(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)result(this.query((String)sql, (PreparedStatementSetter)pss, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper))));
    }

    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)result(this.query(sql, args, argTypes, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper))));
    }

    public <T> List<T> query(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return (List)result(this.query((String)sql, (Object[])args, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper))));
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, @Nullable Object... args) throws DataAccessException {
        return (List)result(this.query((String)sql, (Object[])args, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper))));
    }

    private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor<Object> {
        private final RowCallbackHandler rch;

        public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
            this.rch = rch;
        }

        @Nullable
        public Object extractData(ResultSet rs) throws SQLException {
            while(rs.next()) {
                this.rch.processRow(rs);
            }

            return null;
        }
    }

    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
        private final String sql;

        public SimplePreparedStatementCreator(String sql) {
            Assert.notNull(sql, "SQL must not be null");
            this.sql = sql;
        }

        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }

        public String getSql() {
            return this.sql;
        }
    }
    private static <T> T result(@Nullable T result) {
        Assert.state(result != null, "No result");
        return result;
    }
    protected PreparedStatementSetter newArgPreparedStatementSetter(@Nullable Object[] args) {
        return new ArgumentPreparedStatementSetter(args);
    }

    protected PreparedStatementSetter newArgTypePreparedStatementSetter(Object[] args, int[] argTypes) {
        return new ArgumentTypePreparedStatementSetter(args, argTypes);
    }
}
