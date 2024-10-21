package com.antfie.safedbquery;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

@RestController
public class Controller {

    @Autowired
    private DataSource dataSource;

    @RequestMapping("/")
    public String index(String firstName, String lastName) {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            TestData.populateDB(connection);

            QueryBuilder query = createQuery(firstName, lastName);
            ResultSet results = query.Prepare(connection).executeQuery();
            
            return Utils.renderResults(results);
        } catch(SQLException exception) {
            // Nothing to do.
        } finally {
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch(SQLException exception) {
                // Nothing to do.
            }
        }

        return "Error";
    }

    private QueryBuilder createQuery(String firstName, String lastName) throws SQLException {
        QueryBuilder query = new QueryBuilder("SELECT firstName FROM user");

        if (firstName != null) {
            query.AppendSql("WHERE (firstName =");
            query.AppendParameter(firstName);

            query.AppendSql("OR firstName IN");
            query.AppendParameter(Arrays.asList(firstName, firstName, firstName));

            query.AppendSql("OR firstName LIKE");
            query.AppendParameter("%" + firstName + "%");
            query.AppendSql(")");
        }

        if (lastName != null) {
            if (firstName != null) {
                query.AppendSql("AND");
            } else {
                query.AppendSql("WHERE");
            }

            query.AppendSql("lastName =").AppendParameter(lastName);
        }

        query.AppendSql("ORDER BY id DESC");
        return query;
    }
}