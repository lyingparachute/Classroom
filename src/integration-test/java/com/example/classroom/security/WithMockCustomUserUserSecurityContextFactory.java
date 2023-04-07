package com.example.classroom.security;

import com.example.classroom.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String password = "{noop}" + annotation.password();
        User principal = User.builder()
                .id(1L)
                .firstName(annotation.firstName())
                .lastName(annotation.lastName())
                .password(password)
                .email(annotation.username())
                .role(annotation.role())
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
