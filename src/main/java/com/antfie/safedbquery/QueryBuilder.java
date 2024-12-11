package com.antfie.safedbquery;

import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.util.StringJoiner;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryBuilder {
    private Logger logger = LoggerFactory.getLogger(QueryBuilder.class);
    private StringJoiner query = new StringJoiner(" ");
    private List<QueryParameter> parameters = new ArrayList<QueryParameter>();

    public QueryBuilder() {
        // Nothing to do.
    }

    public QueryBuilder(String baseQuery) {
        query.add(baseQuery);
    }

    public QueryBuilder AppendSql(String sql) {
        query.add(sql);
        return this;
    }

    public QueryBuilder AppendQueryBuilder(QueryBuilder queryBuilder) {
        query.merge(queryBuilder.query);
        parameters.addAll(queryBuilder.parameters);
        return this;
    }

    public QueryBuilder AppendParameter(String parameter) {
        query.add("?");
        parameters.add(new QueryParameter(parameter));
        return this;
    }

    public QueryBuilder AppendParameter(List<String> parameter) {
        query.add("(");

        for (int index = 0; index < parameter.size(); index++) {
            if (index > 0) {
                query.add(",");
            }

            query.add("?");
            parameters.add(new QueryParameter(parameter.get(index)));
        }

        query.add(")");
        return this;
    }

    public PreparedStatement Prepare(Connection connection) throws SQLException {
        if (parameters.size() != StringUtils.countOccurrencesOf(query.toString(), "?")) {
            throw new SQLException("Unexpected number of parameters. Ensure all untrusted data is correclty paramatarised.");
        }

        logger.debug(Encode.forJava(query.toString()));

        PreparedStatement statement = connection.prepareStatement(query.toString());
        PopulateParameters(statement);

        return statement;
    }

    private void PopulateParameters(PreparedStatement statement) throws SQLException {
        for (int index = 0; index < parameters.size(); index++) {
            parameters.get(index).addToStatement(index + 1, statement);
        }
    }
}
