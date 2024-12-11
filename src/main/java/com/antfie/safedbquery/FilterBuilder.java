package com.antfie.safedbquery;

public class FilterBuilder {
    private final String[] allowedColumnNames;

    public FilterBuilder(String[] allowedColumnNames) {
        this.allowedColumnNames = allowedColumnNames;
    }

    public void processTuple(QueryBuilder query, String untrustedFilter) {
        String[] untrustedParts = untrustedFilter.split("=");
        String untrustedColumnName = untrustedParts[0].trim();
        String untrustedValue = untrustedParts[1].trim();

        String validatedColumnName = validateInputAgainstAllowList(untrustedColumnName);

        query.AppendSql("[" + validatedColumnName + "] =");
        query.AppendParameter(untrustedValue);
    }

    private String validateInputAgainstAllowList(String input) {
        for (String allowedOption : this.allowedColumnNames) {
            if (input.equals(allowedOption)) {
                return allowedOption;
            }
        }

        throw new IllegalArgumentException("Invalid input");
    }
}