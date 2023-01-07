package com.example.classroom.dto;

import com.example.classroom.entity.Subject;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.entity.FieldOfStudy} entity
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FieldOfStudyDto implements Serializable {
    private Long id;
    private String name;
    private LevelOfEducation levelOfEducation;
    private ModeOfStudy mode;
    private AcademicTitle title;
    private Set<Subject> subjects = new HashSet<>();
}