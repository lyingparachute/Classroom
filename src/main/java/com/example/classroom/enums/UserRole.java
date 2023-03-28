package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {
    ROLE_STUDENT("STUDENT"),
    ROLE_TEACHER("TEACHER"),
    ROLE_DEAN("DEAN"),
    ROLE_ADMIN("ADMIN");

    public final String name;

}
