package com.poorlex.poorlex.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberArgumentResolver;
import com.poorlex.poorlex.config.auth.argumentresolver.MemberInfo;
import com.poorlex.poorlex.config.auth.interceptor.TokenInterceptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@AutoConfigureRestDocs
public abstract class RestDocsDocumentationTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected TokenInterceptor tokenInterceptor;

    @MockBean
    protected MemberArgumentResolver memberArgumentResolver;

    protected void mockingTokenInterceptor() {
        given(tokenInterceptor.preHandle(any(), any(), any())).willReturn(true);
    }

    protected void mockingMemberArgumentResolver() {
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new MemberInfo(1L));
    }
}
