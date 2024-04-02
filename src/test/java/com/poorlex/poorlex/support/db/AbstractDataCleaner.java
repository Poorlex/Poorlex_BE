package com.poorlex.poorlex.support.db;

import io.jsonwebtoken.lang.Strings;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDataCleaner {

    private static final String CAMEL_CASE_REGEX = "([a-z])([A-Z]+)";
    private static final String SNAKE_CASE_REGEX = "$1_$2";

    protected List<String> tableNames;
    private TestDBType testDBType;

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @PostConstruct
    public void findDatabaseTableNames() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(this::isEntityClass)
                .map(this::convertToTableName)
                .toList();

        final String driverClassName = dataSourceProperties.getDriverClassName();
        testDBType = TestDBType.findTypeByDriverClassName(driverClassName);
    }

    private boolean isEntityClass(final EntityType<?> e) {
        return e.getJavaType().getAnnotation(Entity.class) != null;
    }

    private String convertToTableName(final EntityType<?> e) {
        final Table tableAnnotation = e.getJavaType().getAnnotation(Table.class);
        if (Objects.nonNull(tableAnnotation) && Strings.hasText(tableAnnotation.name())) {
            return tableAnnotation.name();
        }
        return e.getName().replaceAll(CAMEL_CASE_REGEX, SNAKE_CASE_REGEX).toLowerCase();
    }

    @Transactional
    protected void clearEntityManager() {
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    protected void initializeDataBase() {
        entityManager.createNativeQuery(testDBType.getDisableConstraintQuery()).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(testDBType.getTableTruncateQuery(tableName)).executeUpdate();
            entityManager.createNativeQuery(testDBType.getTableIdValueInitializeQuery(tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(testDBType.getEnableConstraintQuery()).executeUpdate();
    }
}
