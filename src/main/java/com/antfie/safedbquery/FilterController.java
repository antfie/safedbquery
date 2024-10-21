package com.antfie.safedbquery;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import org.owasp.encoder.Encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;

@RestController
public class FilterController {

    @Autowired
    private DataSource dataSource;

    private Logger logger = LoggerFactory.getLogger(FilterController.class);

    private static String[] allowedColumnNames = new String[] {
            "firstName",
            "lastName",
            // ...
    };

    private static String AndPredicate = " AND ";
    private static String OrPredicate = " OR ";

    @RequestMapping("/filter/{filter}")
    public String index(@PathVariable("filter") String filter) {
        Connection connection = null;
        logger.info(String.format("Incoming query: \"%s\"", Encode.forJava(filter)));

        try {
            connection = dataSource.getConnection();
            TestData.populateDB(connection);

            QueryBuilder query = new QueryBuilder("SELECT firstName FROM user WHERE");
            parseFilter(query, filter);
            query.AppendSql("ORDER BY id DESC");

            ResultSet results = query.Prepare(connection).executeQuery();

            return Utils.renderResults(results);
        } catch (SQLException exception) {
            logger.error("SQLException", exception);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException exception) {
                logger.error("SQLException closing connection", exception);
            }
        }

        return "Error";
    }

    private void parseFilter(QueryBuilder query, String filter) {
        if (filter.contains(AndPredicate)) {
            logger.info("Processing AND...");

            String[] predicateParts = filter.split(AndPredicate);

            if (predicateParts.length > 2) {
                throw new IllegalArgumentException("Cannot have more than one AND instruction");
            }

            String leftHandSide = predicateParts[0].trim();
            String rightHandSide = predicateParts[1].trim();
            logger.info(String.format("left: %s, right: %s", Encode.forJava(leftHandSide), Encode.forJava(rightHandSide)));

            ProcessTuple(query, leftHandSide);
            query.AppendSql(AndPredicate);
            ProcessTuple(query, rightHandSide);
        } else if (filter.contains(OrPredicate)) {
            logger.info("Processing OR...");

            String[] predicateParts = filter.split(OrPredicate);

            if (predicateParts.length > 2) {
                throw new IllegalArgumentException("Cannot have more than one OR instruction");
            }

            String leftHandSide = predicateParts[0].trim();
            String rightHandSide = predicateParts[1].trim();
            logger.info(String.format("left: %s, right: %s", Encode.forJava(leftHandSide), Encode.forJava(rightHandSide)));

            ProcessTuple(query, leftHandSide);
            query.AppendSql(OrPredicate);
            ProcessTuple(query, rightHandSide);
        } else {
            ProcessTuple(query, filter);
        }
    }

    private static void ProcessTuple(QueryBuilder query, String untrustedFilter) {
        String[] untrustedParts = untrustedFilter.split("=");
        String untrustedColumnName = untrustedParts[0].trim();
        String untrustedValue = untrustedParts[1].trim();
        String validatedColumnName = validateInputAgainstAllowList(allowedColumnNames, untrustedColumnName);

        query.AppendSql("[" + validatedColumnName + "] =");
        query.AppendParameter(untrustedValue);
    }

    private static String validateInputAgainstAllowList(String[] allowList, String input) {
        for (String allowedOption : allowList) {
            if (input.equals(allowedOption)) {
                return allowedOption;
            }
        }

        throw new IllegalArgumentException("Invalid option");
    }
}