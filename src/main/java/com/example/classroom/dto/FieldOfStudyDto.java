package com.example.classroom.dto;

import com.example.classroom.entity.Department;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.FieldOfStudy} entity
 */
@Data
@NoArgsConstructor
public class FieldOfStudyDto {
    private Long id;
    private String name;
    private LevelOfEducation levelOfEducation;
    private ModeOfStudy mode;
    private AcademicTitle title;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department department;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Subject> subjects = new HashSet<>();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Student> students = new HashSet<>();
}