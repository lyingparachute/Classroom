package com.example.classroom.dto;

import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
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
 * A DTO for the {@link com.example.classroom.model.Student} entity
 */
@Data
@NoArgsConstructor
public class StudentDto {
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
