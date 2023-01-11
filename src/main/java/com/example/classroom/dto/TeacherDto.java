package com.example.classroom.dto;

import com.example.classroom.entity.Department;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.Teacher} entity
 */
@Data
@NoArgsConstructor
public class TeacherDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department departmentDean;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Student> students = new HashSet<>();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Subject> subjects = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", subjects=" + subjects.size();
    }
}
