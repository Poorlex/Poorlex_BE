package com.poorlex.poorlex.support.security;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@ImportAutoConfiguration(classes = {MockMvcSpringSecurityTestConfigure.class})
@AutoConfigureMockMvc
public @interface DocumentTestMockMvcConfigure {

}
