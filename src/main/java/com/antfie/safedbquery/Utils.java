package com.antfie.safedbquery;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

     public static String renderResults(ResultSet results) {
        StringBuilder output = new StringBuilder("<ol>");
        int count = 0;

        try {
            while (results.next()) {
                output.append("<li>" + Encode.forHtml(results.getString("firstName")) + "</li>");
                count++;
            }
        } catch(SQLException exception) {
            output.append("<li>Error</li>");
            logger.error("Error calling renderResults", exception);
        } 

        return output.append("</ol><p>Results: ").append(count).append("</p>").toString();
    }
}