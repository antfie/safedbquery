package com.antfie.safedbquery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

@Component
public class DB {

    @Autowired
    private DataSource dataSource;

    private Logger logger = LoggerFactory.getLogger(DB.class);

    private Boolean initialised = false;
    private Connection connection = null;

    @RequestMapping("/")
    public ResultSet execute(QueryBuilder query) {
        try {
            if (!initialised) {
                this.connection = dataSource.getConnection();
                TestData.populateDB(connection);
                initialised = true;
            }

            return query.Prepare(connection).executeQuery();
        } catch(SQLException exception) {
            logger.error("Error calling execute", exception);
        }

        return null;
    }
}