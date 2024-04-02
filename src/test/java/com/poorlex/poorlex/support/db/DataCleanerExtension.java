package com.poorlex.poorlex.support.db;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataCleanerExtension implements BeforeEachCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeEach(final ExtensionContext context) {
        final AbstractDataCleaner dataCleaner = getDataCleaner(context);
        dataCleaner.clearEntityManager();
        dataCleaner.initializeDataBase();
    }

    private AbstractDataCleaner getDataCleaner(ExtensionContext extensionContext) {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);
        final DataSourceProperties dataSourceProperties = applicationContext.getBean(DataSourceProperties.class);
        final String driverClassName = dataSourceProperties.getDriverClassName();
        final TestDBType testDBType = TestDBType.findTypeByDriverClassName(driverClassName);
        logger.info("DataCleaner Loading :: DataCleaner = {}", testDBType.getCleanerClass());
        return SpringExtension.getApplicationContext(extensionContext)
                .getBean(testDBType.getCleanerClass());
    }
}
