package com.poorlex.poorlex.support.db;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class UsingDataJpaTest extends AbstractDataCleaner {

    protected void 영속성_컨텍스트를_플러시하고_초기화한다() {
        clearEntityManager();
    }
}
