package com.example.classroom.dto;

import com.example.classroom.entity.Student;
import com.example.classroom.enums.SubjectEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TeacherDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private SubjectEnum subjectEnum;
    private Set<Student> studentsList = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", subjectEnum=" + subjectEnum.getDesc();
    }
}
