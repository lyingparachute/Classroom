package com.example.classroom.dto;

import com.example.classroom.entity.Department;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
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
    private Department departmentDean;
    private Set<Student> studentsList = new HashSet<>();
    private Set<Subject> subjects = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", subjects=" + subjects.size();
    }
}
