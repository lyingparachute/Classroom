package com.example.classroom.entity;

import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "fieldOfStudy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Subject> subjects;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldOfStudy that = (FieldOfStudy) o;
        return id.equals(that.id) && name.equals(that.name) && levelOfEducation == that.levelOfEducation && mode == that.mode && title == that.title && Objects.equals(subjects, that.subjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, levelOfEducation, mode, title, subjects);
    }

    @Override
    public String toString() {
        return "FieldOfStudy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", levelOfEducation=" + levelOfEducation +
                ", mode=" + mode +
                ", title=" + title +
                '}';
    }
}
