package com.poorlex.poorlex.support.db;

import org.springframework.stereotype.Component;

@Component
public class MySqlDataCleaner extends AbstractDataCleaner {

    private static final String MYSQL_SET_FOREIGN_KEY = "SET FOREIGN_KEY_CHECKS = %d";
    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
    private static final String ALTER_TABLE_FORMAT = "ALTER TABLE %s AUTO_INCREMENT = 1";

    @Override
    protected void truncate() {
        entityManager.createNativeQuery(String.format(MYSQL_SET_FOREIGN_KEY, 0)).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(ALTER_TABLE_FORMAT, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format(MYSQL_SET_FOREIGN_KEY, 1)).executeUpdate();
    }
}
