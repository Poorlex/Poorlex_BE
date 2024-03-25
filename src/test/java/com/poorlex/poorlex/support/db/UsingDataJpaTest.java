package com.poorlex.poorlex.support.db;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public abstract class UsingDataJpaTest {

    @Autowired
    private EntityManager entityManager;

    protected void 영속성_컨텍스트를_플러시하고_초기화한다() {
        entityManager.flush();
        entityManager.clear();
    }
}
