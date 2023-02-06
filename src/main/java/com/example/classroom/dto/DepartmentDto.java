package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.Department} entity
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