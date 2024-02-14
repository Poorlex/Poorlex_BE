package com.poorlex.poorlex.support.security;

import org.springframework.security.test.context.support.WithMockUser;

@WithMockUser(roles = "USER")
public interface MockUserSecurityTest {

}
