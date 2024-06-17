package com.poorlex.poorlex.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorlex.poorlex.security.SecurityConfig;
import com.poorlex.poorlex.security.service.MemberInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@WebMvcTest(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
@AutoConfigureRestDocs
public abstract class MockMvcTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUpUserDetails() {
        MemberInfo memberInfo = MemberInfo.ofUserRole(1L);
        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(memberInfo, "ACCESS TOKEN", memberInfo.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(user);
    }
}
