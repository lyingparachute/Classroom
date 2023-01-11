package com.example.classroom.entity;


import com.example.classroom.enums.Semester;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "field_id")
    @ToString.Exclude
    private FieldOfStudy fieldOfStudy;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "subjects")
    @ToString.Exclude
    private Set<Teacher> teachers = new HashSet<>();

    /**
     * Add new Teacher. The method keeps relationships consistency:
     * * this subject is added to subjects
     *   on the teacher side
     */
    public void addTeacher(Teacher teacher) {
        //prevent endless loop
        if (teachers.contains(teacher)) {
            return;
        }
        teachers.add(teacher);
        teacher.addSubject(this);
    }

    /**
     * Remove Teacher. The method keeps relationships consistency:
     * * this subject is removed from subjects
     *   on the teacher side
     */
    public void removeTeacher(Teacher teacher) {
        //prevent endless loop
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
        return id.equals(subject.id) && Objects.equals(name, subject.name) && Objects.equals(description, subject.description) && Objects.equals(teachers, subject.teachers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, teachers);
    }
}
