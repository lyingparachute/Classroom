package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.Semester;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.Subject} entity
 */
@Data
@NoArgsConstructor
public class SubjectDto {
    private Long id;
    private String name;
    private String description;
    private Semester semester;
    private int hoursInSemester;
    private int ectsPoints;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldOfStudy fieldOfStudy;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachers = new HashSet<>();
}
