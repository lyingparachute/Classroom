package com.example.classroom.dto;

import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.enums.Semester;
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
    private String name;
    private String description;
    private Semester semester;
    private int hoursInSemester;
    private FieldOfStudy fieldOfStudy;
    private Set<Teacher> teachers = new HashSet<>();

    @Override
    public String toString() {
        return "SubjectDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
