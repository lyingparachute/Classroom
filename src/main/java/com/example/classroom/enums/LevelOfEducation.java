package com.example.classroom.enums;

public enum LevelOfEducation {
    FIRST("First Degree Studies"),
    SECOND("Second Degree Studies");

    private final String value;
    LevelOfEducation(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
