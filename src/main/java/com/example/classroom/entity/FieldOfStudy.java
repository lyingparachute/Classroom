package com.example.classroom.entity;

import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "fields_of_study")
public class FieldOfStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LevelOfEducation levelOfEducation;

    @Column(name = "study_mode")
    @Enumerated(EnumType.STRING)
    private ModeOfStudy mode;

    @Column(name = "obtained_title")
    @Enumerated(EnumType.STRING)
    private AcademicTitle title;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    private Department department;

    @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private Set<Subject> subjects = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldOfStudy that = (FieldOfStudy) o;
        return id.equals(that.id) && name.equals(that.name) && levelOfEducation == that.levelOfEducation && mode == that.mode && title == that.title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, levelOfEducation, mode, title);
    }
}
