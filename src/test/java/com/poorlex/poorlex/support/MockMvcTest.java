package com.poorlex.poorlex.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.interceptor.TokenInterceptor;
import com.poorlex.poorlex.security.SecurityConfig;
import com.poorlex.poorlex.support.security.MockUserSecurityTest;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureRestDocs
public abstract class MockMvcTest implements MockUserSecurityTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected TokenInterceptor tokenInterceptor;

    @MockBean
    protected MemberArgumentResolver memberArgumentResolver;

    protected void mockingTokenInterceptor() throws IOException {
        given(tokenInterceptor.preHandle(any(), any(), any())).willReturn(true);
    }

    protected void mockingMemberArgumentResolver() {
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new MemberInfo(1L));
    }
}
