package com.antfie.safedbquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestData {
    public static void populateDB(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS user");
        statement.executeUpdate("CREATE TABLE user (id integer, firstName string, lastName string, password string)");
        statement.executeUpdate("INSERT INTO user VALUES(1, 'ada', 'lovelace', 'secret123')");
        statement.executeUpdate("INSERT INTO user VALUES(2, 'bill', 'gates', '123456')");
        statement.close();
    }
}