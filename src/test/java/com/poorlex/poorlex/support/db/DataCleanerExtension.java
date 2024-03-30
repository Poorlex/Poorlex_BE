package com.poorlex.poorlex.support.db;

import java.util.Arrays;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataCleanerExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final AbstractDataCleaner dataCleaner = getDataCleaner(context);
        dataCleaner.clear();
    }

    private AbstractDataCleaner getDataCleaner(ExtensionContext extensionContext) {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        final DataSourceProperties dataSourceProperties = applicationContext.getBean(DataSourceProperties.class);
        final String driverClassName = dataSourceProperties.getDriverClassName();
        final Class<? extends AbstractDataCleaner> cleanerType = DBCleaner.findTypeByDriverClassName(driverClassName);

        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(cleanerType);
    }

    private enum DBCleaner {
        MYSQL("com.mysql.cj.jdbc.Driver", MySqlDataCleaner.class),
        H2("org.h2.Driver", H2DataCleaner.class);

        private final String driverClassName;
        private final Class<? extends AbstractDataCleaner> cleanerClass;

        DBCleaner(final String driverClassName, final Class<? extends AbstractDataCleaner> cleanerClass) {
            this.driverClassName = driverClassName;
            this.cleanerClass = cleanerClass;
        }

        public static Class<? extends AbstractDataCleaner> findTypeByDriverClassName(final String driverClassName) {
            final DBCleaner dbCleaner = Arrays.stream(values())
                    .filter(driver -> driver.driverClassName.equals(driverClassName))
                    .findFirst()
                    .orElseThrow(() -> {
                        final String errorMessage = String.format("No dataCleaner for driver ( driver = %s )",
                                                                  driverClassName);
                        return new IllegalArgumentException(errorMessage);
                    });

            return dbCleaner.cleanerClass;
        }
    }
}
