package com.poorlex.poorlex.support.db;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataCleanerExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final DataCleaner dataCleaner = getDataCleaner(context);
        dataCleaner.clear();
    }

    private DataCleaner getDataCleaner(ExtensionContext extensionContext) {
        return SpringExtension.getApplicationContext(extensionContext)
            .getBean(DataCleaner.class);
    }
}
