package org.init.aop.support;


import org.init.UncategorizedSQLException;
import org.init.core.dao.DataAccessException;
import org.init.core.lang.Nullable;

import java.sql.SQLException;

public abstract class AbstractFallbackSQLExceptionTranslator implements SQLExceptionTranslator{
    @Nullable
    private SQLExceptionTranslator fallbackTranslator;

    public AbstractFallbackSQLExceptionTranslator() {
    }

    public void setFallbackTranslator(@Nullable SQLExceptionTranslator fallback) {
        this.fallbackTranslator = fallback;
    }

    @Nullable
    public SQLExceptionTranslator getFallbackTranslator() {
        return this.fallbackTranslator;
    }


    public DataAccessException translate(String task, @Nullable String sql, SQLException ex) {
        if (ex == null){
            System.out.println("Cannot translate a null SQLException");
        }
        DataAccessException dae = this.doTranslate(task, sql, ex);
        if (dae != null) {
            return dae;
        } else {
            SQLExceptionTranslator fallback = this.getFallbackTranslator();
            if (fallback != null) {
                dae = fallback.translate(task, sql, ex);
                if (dae != null) {
                    return dae;
                }
            }

            return new UncategorizedSQLException(task, sql, ex);
        }
    }

    @Nullable
    protected abstract DataAccessException doTranslate(String var1, @Nullable String var2, SQLException var3);

    protected String buildMessage(String task, @Nullable String sql, SQLException ex) {
        return task + "; " + (sql != null ? "SQL [" + sql : "]; ") + ex.getMessage();
    }
}
