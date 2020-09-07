# Safe DB Query

this is a demonstration of how to perform complex DB queries using paramatarisation in order to protect against SQL injection attacks. The demo uses Spring Boot, JNDI and SQLite.

It is a good idea to consider using the DAO pattern, using models for the tables and repositories for accessing data. A useful article on this can be found here: <https://javadeveloperzone.com/spring-boot/spring-boot-jndi-datasource-example>. You may also want to consider the `PreparedStatementCreatorFactory` <https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/PreparedStatementCreatorFactory.html>.

## Demonstration

Using the two supplied classes [QueryBuilder.java](/src/main/java/com/antfie/safedbquery/QueryBuilder.java) and [QueryParameter.java](/src/main/java/com/antfie/safedbquery/QueryParameter.java) it is easy to build complex DB queries as shown below. You can use these two simple classes in your own projects.

```java
// Build the query
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

// Execute
ResultSet results = query.Prepare(connection).executeQuery();
```

## Running

Build and run using Maven with:

```
mvn spring-boot:run
```

Browse to <http://localhost:8080/?firstName=ada&lastName=lovelace>.

## Dependencies For Query Code

The `org.springframework.util.StringUtils` class is used as a sanity check to ensure there are the correct number of parameters for a query. If this dependency is not wanted, the check can be removed.

## Dependencies For Demo

* Spring Boot
* org.xerial.sqlite-jdbc
* org.owasp.encoder
