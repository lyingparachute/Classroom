package com.example.classroom.dto;

import com.example.classroom.model.Department;
import com.example.classroom.model.Student;
import com.example.classroom.model.Subject;
import com.example.classroom.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.model.Teacher} entity
 */
@Data
@NoArgsConstructor
public class TeacherDto {
    private Long id;
    @NotNull
    @NotEmpty(message = "{message.firstName.empty}")
    @Length(min = 2, max = 30, message = "{message.firstName.length}")
    private String firstName;
    @NotNull
    @NotEmpty(message = "{message.lastName.empty}")
    @Length(min = 2, max = 30, message = "{message.lastName.length}")
    private String lastName;
    @NotNull
    @Min(value = 21, message = "{teacher.age.min}")
    @Max(value = 80, message = "{teacher.age.max}")
    private int age;
    @NotNull
    @NotEmpty(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    private String email;
    private User userDetails;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department department;
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
