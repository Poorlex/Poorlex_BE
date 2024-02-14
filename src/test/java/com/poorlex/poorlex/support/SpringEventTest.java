package com.poorlex.poorlex.support;

import com.poorlex.poorlex.support.db.DataCleanerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@ExtendWith(DataCleanerExtension.class)
@RecordApplicationEvents
public abstract class SpringEventTest {

    @Autowired
    protected ApplicationEvents events;
    
}
