package com.example.classroom.enums;


import lombok.Getter;

@Getter
public enum SubjectEnum {
    MATHS("Mathematics"),
    SCIENCE("Science"),
    ART("Art"),
    IT("Computer Science");

    private final String desc;
    SubjectEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
