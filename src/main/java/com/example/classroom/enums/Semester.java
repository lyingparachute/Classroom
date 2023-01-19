package com.example.classroom.enums;

public enum Semester {
    FIRST("I"),
    SECOND("II"),
    THIRD("III"),
    FOURTH("IV"),
    FIFTH("V"),
    SIXTH("VI"),
    SEVENTH("VII");

    private final String value;
    Semester(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
