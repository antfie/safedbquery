package com.antfie.safedbquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestData {
    public static void populateDB(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS user");
        statement.executeUpdate("CREATE TABLE user (id integer, firstName string, lastName string)");
        statement.executeUpdate("INSERT INTO user VALUES(1, 'ada', 'lovelace')");
        statement.executeUpdate("INSERT INTO user VALUES(2, 'bill', 'gates')");
        statement.close();
    }
}