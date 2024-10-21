package com.antfie.safedbquery;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.owasp.encoder.Encode;

public class Utils {
     public static String renderResults(ResultSet results) throws SQLException {
        StringBuilder output = new StringBuilder("<ol>");

        while (results.next()) {
            output.append("<li>" + Encode.forHtml(results.getString("firstName")) + "</li>");
        }

        return output.append("</ol>").toString();
    }
}
