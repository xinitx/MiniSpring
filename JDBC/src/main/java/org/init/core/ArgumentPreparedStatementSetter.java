package org.init.core;

import org.init.core.lang.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArgumentPreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer  {
    @Nullable
    private final Object[] args;
    public ArgumentPreparedStatementSetter(@Nullable Object[] args) {
        this.args = args;
    }
    @Override
    public void cleanupParameters() {

    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        if (this.args != null) {
            for(int i = 0; i < this.args.length; ++i) {
                Object arg = this.args[i];
                this.doSetValue(ps, i + 1, arg);
            }
        }
    }
    protected void doSetValue(PreparedStatement ps, int parameterPosition, Object argValue) throws SQLException {
        if (argValue instanceof String) {
            ps.setString(parameterPosition, (String)argValue);
        }
        else if (argValue instanceof Integer) {
            ps.setInt(parameterPosition, (int)argValue);
        }
        else if (argValue instanceof java.util.Date) {
            ps.setDate(parameterPosition, new java.sql.Date(((java.util.Date)argValue).getTime()));

        }

    }
}
