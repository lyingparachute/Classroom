package com.example.classroom.enums;

public enum ModeOfStudy {
    FT("Full-time"),
    PT("Part-time");

    private final String value;
    ModeOfStudy(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
