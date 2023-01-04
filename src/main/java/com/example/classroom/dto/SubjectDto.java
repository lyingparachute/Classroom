package com.example.classroom.dto;

import com.example.classroom.entity.Teacher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectDto {

    private Long id;
    private String name;
    private String description;
    private Set<Teacher> teachers = new HashSet<>();
}
