package com.example.classroom.dto;

import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.model.Department} entity
 */
@Data
@NoArgsConstructor
public class DepartmentDto {
    private Long id;
    @NotNull
    @Size(min = 10, max = 50, message = "{department.name.size}")
    private String name;

    private String address;

    @NotEmpty(message = "{department.telNumber.empty}")
    @Pattern(regexp = "\\d{9}", message = "{department.telNumber.size}")
    private String telNumber;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Teacher dean;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FieldOfStudy> fieldsOfStudy = new HashSet<>();
}