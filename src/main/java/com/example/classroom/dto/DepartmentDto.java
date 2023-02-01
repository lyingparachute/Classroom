package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.Department} entity
 */
@Data
@NoArgsConstructor
public class DepartmentDto {
    private Long id;
    private String name;
    private String address;
    //    @Pattern(regexp = "(\\+48|0)\\d{9}")
    private Integer telNumber;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Teacher dean;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FieldOfStudy> fieldsOfStudy = new HashSet<>();
}