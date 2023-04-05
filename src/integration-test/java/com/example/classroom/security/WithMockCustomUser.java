package com.example.classroom.security;

import com.example.classroom.user.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "andrzej.nowak@gmail.com";

    String password() default "password";

    String firstName() default "Firstname";

    String lastName() default "Lastname";

    UserRole role() default UserRole.ROLE_ADMIN;
}
