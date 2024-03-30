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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public abstract class AbstractDataCleaner {

    private static final String CAMEL_CASE_REGEX = "([a-z])([A-Z]+)";
    private static final String SNAKE_CASE_REGEX = "$1_$2";

    protected List<String> tableNames;

    @PersistenceContext
    protected EntityManager entityManager;

    @PostConstruct
    public void findDatabaseTableNames() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(AbstractDataCleaner::isEntityClass)
                .map(AbstractDataCleaner::convertToTableName)
                .toList();
    }

    private static boolean isEntityClass(final EntityType<?> e) {
        return e.getJavaType().getAnnotation(Entity.class) != null;
    }

    private static String convertToTableName(final EntityType<?> e) {
        final Table tableAnnotation = e.getJavaType().getAnnotation(Table.class);
        if (Objects.nonNull(tableAnnotation) && Strings.hasText(tableAnnotation.name())) {
            return tableAnnotation.name();
        }
        return e.getName().replaceAll(CAMEL_CASE_REGEX, SNAKE_CASE_REGEX).toLowerCase();
    }

    @Transactional
    public void clear() {
        entityManager.flush();
        entityManager.clear();
        truncate();
    }

    protected abstract void truncate();
}
