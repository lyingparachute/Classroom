package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
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
