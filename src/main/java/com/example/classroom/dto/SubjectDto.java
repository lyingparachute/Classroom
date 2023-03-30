package com.example.classroom.dto;

import com.example.classroom.enums.Semester;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Teacher;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.example.classroom.model.Subject} entity
 */
@Data
@NoArgsConstructor
public class SubjectDto {
    private Long id;
    @NotEmpty(message = "{message.name.empty}")
    @Length(min = 2, max = 30, message = "{message.name.length}")
    private String name;
    @Length(max = 500, message = "{message.description.length}")
    private String description;
    @Enumerated(EnumType.STRING)
    private Semester semester;
    @PositiveOrZero
    @Max(value = 100, message = "{subject.hoursInSemester.max}")
    private int hoursInSemester;
    @Max(value = 60, message = "{subject.ects.max}")
    @Min(value = 5, message = "{subject.ects.min}")
    private int ectsPoints;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldOfStudy fieldOfStudy;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachers = new HashSet<>();
}
