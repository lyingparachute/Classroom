package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.Semester;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class SubjectDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Semester semester;
    private int hoursInSemester;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FieldOfStudy fieldOfStudy;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachers = new HashSet<>();
}
