package com.example.classroom.model;


import com.example.classroom.enums.Semester;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Length(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    @PositiveOrZero
    private int hoursInSemester;

    @Max(30)
    @Min(0)
    @Column(name = "ects")
    private int ectsPoints;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    @ToString.Exclude
    private FieldOfStudy fieldOfStudy;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "subjects")
    @ToString.Exclude
    private Set<Teacher> teachers = new HashSet<>();

    public Set<Teacher> getTeachers() {
        return new HashSet<>(teachers);
    }

    /**
     * Set new Field Of Study's department. The method keeps
     * relationships consistency:
     * * this Field Of Study is removed from the previous Department
     * * this Field Of Study is added to next Department
     */
    public void setFieldOfStudy(FieldOfStudy fieldOfStudy) {
        if (isSameAsFormer(fieldOfStudy))
            return;
        FieldOfStudy oldFieldOfStudy = this.fieldOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        if (oldFieldOfStudy != null)
            oldFieldOfStudy.removeSubject(this);
        if (fieldOfStudy != null) {
            fieldOfStudy.addSubject(this);
        }
    }

    private boolean isSameAsFormer(FieldOfStudy newFieldOfStudy) {
        if (fieldOfStudy == null)
            return newFieldOfStudy == null;
        return fieldOfStudy.equals(newFieldOfStudy);
    }

    /**
     * Add new Teacher. The method keeps relationships consistency:
     * * this subject is added to subjects
     * on the teacher side
     */
    public void addTeacher(Teacher teacher) {
        if (teachers.contains(teacher)) {
            return;
        }
        teachers.add(teacher);
        teacher.addSubject(this);
    }

    /**
     * Remove Teacher. The method keeps relationships consistency:
     * * this subject is removed from subjects
     * on the teacher side
     */
    public void removeTeacher(Teacher teacher) {
        if (!teachers.contains(teacher)) {
            return;
        }
        teachers.remove(teacher);
        teacher.removeSubject(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return hoursInSemester == subject.hoursInSemester && id.equals(subject.id) && Objects.equals(name, subject.name) && Objects.equals(description, subject.description) && semester == subject.semester;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, semester, hoursInSemester);
    }
}
