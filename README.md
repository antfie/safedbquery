# Safe DB Query

this is a demonstration of how to perform complex DB queries using paramatarisation in order to protect against SQL injection attacks. The demo uses Spring Boot, JNDI and SQLite. As of 11th December 2024 no security findings were reported by Veracode.

It is a good idea to consider using the DAO pattern, using models for the tables and repositories for accessing data. A useful article on this can be found here: <https://javadeveloperzone.com/spring-boot/spring-boot-jndi-datasource-example>. You may also want to consider the `PreparedStatementCreatorFactory` <https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/PreparedStatementCreatorFactory.html>.

## Demonstration

Using these two simple classes [QueryBuilder.java](/src/main/java/com/antfie/safedbquery/QueryBuilder.java) and [QueryParameter.java](/src/main/java/com/antfie/safedbquery/QueryParameter.java) it is easy to build complex DB queries as shown below. You can use these classes in your own projects.

```java
// Build the query
QueryBuilder query = new QueryBuilder("SELECT firstName FROM user WHERE");

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

query.AppendSql("ORDER BY id DESC");

// Execute
ResultSet results = query.Prepare(connection).executeQuery();
```

There is also an example of securely working with dynamic queries where the column name comes from an untrusted source, e.g. for sorting. To make this safe the untrusted column names are validated against an allow list. You can see this in `FilterController.java` which uses `FilterBuilder.java`.

## Running

You can use Docker to run this, though not required:

```bash
docker pull maven
docker run --rm -it -v "$(pwd):/app" -w /app -p 127.0.0.1:8080:8080 maven mvn spring-boot:run
```

Then browse to:

- <http://localhost:8080/?firstName=ada&lastName=lovelace>.
- <http://localhost:8080/filter/lastName=lovelace>.
- <http://localhost:8080/filter/lastName=lovelace%20OR%20firstName=bill>.
- <http://localhost:8080/filter/lastName=lovelace%20OR%20password=123456>.

## Dependencies

The following dependencies are in use:

- `org.springframework.boot.spring-boot-starter-web`
- `org.xerial.sqlite-jdbc`
- `org.owasp.encoder`
