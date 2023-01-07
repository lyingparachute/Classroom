package com.example.classroom.enums;

public enum FieldOfStudy {
    ROBOTICS("Automatic Control, Cybernetics and Robotics"),
    ELECTRONICS("Electronics and Telecommunications"),
    ELECTRICAL("Electrical Engineering"),
    INFORMATICS("Distributed Applications and Internet Services");

    private final String desc;
    FieldOfStudy (String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
