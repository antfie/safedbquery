package com.antfie.safedbquery;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryParameter {
    private Object value;

    public QueryParameter(Object value) {
        this.value = value;
    }

    public void addToStatement(int parameterIndex, PreparedStatement statement) throws SQLException {
        if (value instanceof String) {
            statement.setString(parameterIndex, (String)value);
            return;
        }

        throw new SQLException("Unsupported type.");
    }
}