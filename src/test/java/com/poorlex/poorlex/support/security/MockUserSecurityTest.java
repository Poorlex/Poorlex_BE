package com.poorlex.poorlex.support.security;

import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(roles = "USER", username = "1")
public interface MockUserSecurityTest {

}
