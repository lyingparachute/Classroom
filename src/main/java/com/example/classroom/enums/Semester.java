package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Semester {
    FIRST("I", "Semester: I"),
    SECOND("II", "Semester: II"),
    THIRD("III", "Semester: III"),
    FOURTH("IV", "Semester: IV"),
    FIFTH("V", "Semester: V"),
    SIXTH("VI", "Semester: VI"),
    SEVENTH("VII", "Semester: VIII");

    private final String value;
    private final String description;
}
