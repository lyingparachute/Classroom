package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ModeOfStudy {
    FT("Full-time"),
    PT("Part-time");

    private final String value;
}
