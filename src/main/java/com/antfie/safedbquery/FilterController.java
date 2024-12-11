package com.antfie.safedbquery;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;

@RestController
public class FilterController {

    @Autowired
    private DB db;

    private Logger logger = LoggerFactory.getLogger(FilterController.class);

    private static String[] allowedColumnNames = new String[] {
            "firstName",
            "lastName",
            // ...
    };

    private final FilterBuilder filterBuilder;

    public FilterController() {
        this.filterBuilder = new FilterBuilder(allowedColumnNames);
    }

    private static String AndPredicate = " AND ";
    private static String OrPredicate = " OR ";

    @RequestMapping("/filter/{filter}")
    public String index(@PathVariable("filter") String filter) {
        logger.info(String.format("Incoming query: \"%s\"", Encode.forJava(filter)));

        QueryBuilder query = new QueryBuilder("SELECT firstName FROM user WHERE");
        parseFilter(query, filter);
        query.AppendSql("ORDER BY id DESC");

        ResultSet results = db.execute(query);
        return Utils.renderResults(results);
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
            logLeftAndRight(leftHandSide, rightHandSide);

            filterBuilder.processTuple(query, leftHandSide);
            query.AppendSql(AndPredicate);
            filterBuilder.processTuple(query, rightHandSide);
        } else if (filter.contains(OrPredicate)) {
            logger.info("Processing OR...");

            String[] predicateParts = filter.split(OrPredicate);

            if (predicateParts.length > 2) {
                throw new IllegalArgumentException("Cannot have more than one OR instruction");
            }

            String leftHandSide = predicateParts[0].trim();
            String rightHandSide = predicateParts[1].trim();
            logLeftAndRight(leftHandSide, rightHandSide);

            filterBuilder.processTuple(query, leftHandSide);
            query.AppendSql(OrPredicate);
            filterBuilder.processTuple(query, rightHandSide);
        } else {
            filterBuilder.processTuple(query, filter);
        }
    }

    private void logLeftAndRight(String leftHandSide, String rightHandSide) {
        logger.info(String.format("left: %s, right: %s", Encode.forJava(leftHandSide), Encode.forJava(rightHandSide)));
    }
}