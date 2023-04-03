package com.example.classroom.student;

import com.example.classroom.fieldOfStudy.FieldOfStudy;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.user.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link Student} entity
 */
@Data
@NoArgsConstructor
class StudentDto {
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
    @Min(value = 18, message = "{student.age.min}")
    @Max(value = 50, message = "{student.age.max}")
    private int age;
    @NotNull
    @NotEmpty(message = "{message.email.empty}")
    @Email(message = "{message.email.valid}")
    private String email;

    private User userDetails;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldOfStudy fieldOfStudy;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachers = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
