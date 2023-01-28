package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LevelOfEducation {
    FIRST("First degree studies"),
    SECOND("Second degree studies");

    private final String value;
}
