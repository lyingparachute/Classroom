package com.example.classroom.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    ROLE_STUDENT("STUDENT", "You are a Student"),
    ROLE_TEACHER("TEACHER", "You are a Professor"),
    ROLE_DEAN("DEAN", "You are a Professor and Dean"),
    ROLE_ADMIN("ADMIN", "ADMIN ACCOUNT");

    public final String name;
    public final String description;
}
