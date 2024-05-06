package org.init.core;

import org.init.core.lang.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArgumentTypePreparedStatementSetter implements PreparedStatementSetter, ParameterDisposer  {
    @Nullable
    private final Object[] args;
    @Nullable
    private final int[] argTypes;
    public ArgumentTypePreparedStatementSetter(@Nullable Object[] args, @Nullable int[] argTypes) {
        if ((args == null || argTypes != null) && (args != null || argTypes == null) && (args == null || args.length == argTypes.length)) {
            this.args = args;
            this.argTypes = argTypes;
        } else {
            throw new IllegalArgumentException("args and argTypes parameters must match");
        }
    }
    @Override
    public void cleanupParameters() {

    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {

    }
}
