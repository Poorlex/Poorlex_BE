package com.poorlex.poorlex.support.db;

import org.springframework.stereotype.Component;

@Component
public class H2DataCleaner extends AbstractDataCleaner {

    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
    private static final String H2_SET_FOREIGN_KEY = "SET REFERENTIAL_INTEGRITY %s";
    private static final String ALTER_TABLE_FORMAT = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";

    @Override
    protected void truncate() {
        entityManager.createNativeQuery(String.format(H2_SET_FOREIGN_KEY, "FALSE")).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(ALTER_TABLE_FORMAT, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format(H2_SET_FOREIGN_KEY, "TRUE")).executeUpdate();
    }
}
