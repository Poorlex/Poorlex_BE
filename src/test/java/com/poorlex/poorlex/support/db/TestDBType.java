package com.poorlex.poorlex.support.db;

import java.util.Arrays;

public enum TestDBType {
    MYSQL("com.mysql.cj.jdbc.Driver",
          "TRUNCATE TABLE %s",
          "SET FOREIGN_KEY_CHECKS = 0",
          "SET FOREIGN_KEY_CHECKS = 1",
          "ALTER TABLE %s AUTO_INCREMENT = 1",
          MySqlDataCleaner.class),
    H2("org.h2.Driver",
       "TRUNCATE TABLE %s",
       "SET REFERENTIAL_INTEGRITY FALSE",
       "SET REFERENTIAL_INTEGRITY TRUE",
       "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1",
       H2DataCleaner.class);

    private final String driverClassName;
    private final String truncateQuery;
    private final String disableConstraintQuery;
    private final String enableConstraintQuery;
    private final String autoIncrementInitializeQuery;
    private final Class<? extends AbstractDataCleaner> cleanerClass;

    TestDBType(final String driverClassName,
               final String truncateQuery,
               final String disableConstraintQuery,
               final String enableConstraintQuery,
               final String autoIncrementInitializeQuery,
               final Class<? extends AbstractDataCleaner> cleanerClass) {
        this.driverClassName = driverClassName;
        this.truncateQuery = truncateQuery;
        this.disableConstraintQuery = disableConstraintQuery;
        this.enableConstraintQuery = enableConstraintQuery;
        this.autoIncrementInitializeQuery = autoIncrementInitializeQuery;
        this.cleanerClass = cleanerClass;
    }

    public static TestDBType findTypeByDriverClassName(final String driverClassName) {
        return Arrays.stream(values())
                .filter(driver -> driver.driverClassName.equals(driverClassName))
                .findFirst()
                .orElseThrow(() -> {
                    final String errorMessage = String.format("No dataCleaner for driver ( driver = %s )",
                                                              driverClassName);
                    return new IllegalArgumentException(errorMessage);
                });
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getTableTruncateQuery(final String tableName) {
        return String.format(truncateQuery, tableName);
    }

    public String getDisableConstraintQuery() {
        return disableConstraintQuery;
    }

    public String getEnableConstraintQuery() {
        return enableConstraintQuery;
    }

    public String getTableIdValueInitializeQuery(final String table) {
        return String.format(autoIncrementInitializeQuery, table);
    }

    public Class<? extends AbstractDataCleaner> getCleanerClass() {
        return cleanerClass;
    }
}
