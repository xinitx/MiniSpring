package org.init.aop.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.init.beans.factory.InitializingBean;
import org.init.core.lang.Nullable;

import javax.sql.DataSource;

public abstract class JdbcAccessor implements InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private DataSource dataSource;
    /*@Nullable
    private volatile SQLExceptionTranslator exceptionTranslator;*/
    private boolean lazyInit = true;
    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }
    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        if (dataSource == null){
            System.out.println("dataSource is null");
        }
        return dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        } else {
            if (!this.isLazyInit()) {
                //this.getExceptionTranslator();
            }
        }
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }
}
