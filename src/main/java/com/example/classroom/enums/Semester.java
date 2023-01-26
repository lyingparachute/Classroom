package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Semester {
    FIRST("I", "I (first)", "Semester: I"),
    SECOND("II", "II (second)", "Semester: II"),
    THIRD("III", "III (third)", "Semester: III"),
    FOURTH("IV", "IV (fourth)", "Semester: IV"),
    FIFTH("V", "V (fifth)", "Semester: V"),
    SIXTH("VI", "VI (sixth)", "Semester: VI"),
    SEVENTH("VII", "VII (seventh)", "Semester: VIII");

    private final String value;
    private final String fullName;
    private final String description;
}
