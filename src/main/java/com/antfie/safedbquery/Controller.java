package com.antfie.safedbquery;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.sql.ResultSet;

@RestController
public class Controller {

    @Autowired
    private DB db;

    @RequestMapping("/")
    public String index(String firstName, String lastName) {
        QueryBuilder query = new QueryBuilder("SELECT firstName FROM user WHERE");
        populateQuery(query, firstName, lastName);
        query.AppendSql("ORDER BY id DESC");
        
        ResultSet results = db.execute(query);
        return Utils.renderResults(results);
    }

    private static void populateQuery(QueryBuilder query, String firstName, String lastName) {
        if (firstName != null) {
            query.AppendSql("firstName =");
            query.AppendParameter(firstName);

            query.AppendSql("OR firstName IN");
            query.AppendParameter(Arrays.asList(firstName, firstName, firstName));

            query.AppendSql("OR firstName LIKE");
            query.AppendParameter("%" + firstName + "%");
        }

        if (lastName != null) {
            if (firstName != null) {
                query.AppendSql("AND");
            }

            query.AppendSql("lastName =").AppendParameter(lastName);
        }
    }
}