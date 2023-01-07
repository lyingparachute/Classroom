package com.example.classroom.dto;

import com.example.classroom.entity.Subject;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.FieldOfStudy} entity
 */
@Data
public class FieldOfStudyDto implements Serializable {
    private final Long id;
    private final String name;
    private final LevelOfEducation levelOfEducation;
    private final ModeOfStudy mode;
    private final AcademicTitle title;
    private final Set<Subject> subjects = new HashSet<>();
}