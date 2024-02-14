package com.poorlex.poorlex.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorlex.poorlex.support.db.DataCleanerExtension;
import com.poorlex.poorlex.support.security.MockUserSecurityTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ExtendWith(DataCleanerExtension.class)
public abstract class IntegrationTest implements MockUserSecurityTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
