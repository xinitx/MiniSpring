package org.init.core;

import org.init.core.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>>  {
    private final RowMapper<T> rowMapper;
    private final int rowsExpected;
    public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
        this(rowMapper, 0);
    }
    public RowMapperResultSetExtractor(RowMapper<T> rowMapper, int rowsExpected) {
        if (rowMapper == null){
            throw new IllegalArgumentException("RowMapper is required");
        }
        this.rowMapper = rowMapper;
        this.rowsExpected = rowsExpected;
    }
    @Override
    public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<T> results = this.rowsExpected > 0 ? new ArrayList(this.rowsExpected) : new ArrayList();
        int rowNum = 0;

        while(rs.next()) {
            results.add(this.rowMapper.mapRow(rs, rowNum++));
        }

        return results;
    }
}