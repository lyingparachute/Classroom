package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LevelOfEducation {
    FIRST("First Degree Studies"),
    SECOND("Second Degree Studies");

    private final String value;
}
