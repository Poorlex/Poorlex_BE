package com.poorlex.poorlex.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class MemberInfo extends User {

    private final Long id;

    public MemberInfo(String id, Collection<? extends GrantedAuthority> authorities) {
        super(id, "", authorities);
        super.eraseCredentials();
        this.id = Long.parseLong(id);
    }

    public static MemberInfo ofUserRole(Long id) {
        return new MemberInfo(id.toString(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public Long getId() {
        return id;
    }
}
