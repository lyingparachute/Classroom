package com.example.classroom.dto;

import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.FieldOfStudy;
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
public class StudentDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private FieldOfStudy fieldOfStudy;
    private Set<Teacher> teachersList = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
