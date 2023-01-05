package com.example.classroom.dto;

import com.example.classroom.entity.Teacher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectDto implements Serializable {

    private Long id;
    private String shortName;
    private String longName;
    private int hoursInSemester;
    private String description;
    private Set<Teacher> teachers = new HashSet<>();
}
