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
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH
            },
            mappedBy = "subjects")
    @ToString.Exclude
    private Set<Teacher> teachers = new HashSet<>();


    public void removeTeacher(Teacher teacher) {
        this.teachers.remove(teacher);
        teacher.getSubjects().remove(this);
    }

    public void addTeacher(Teacher teacher) {
        this.teachers.add(teacher);
        teacher.getSubjects().add(this);
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
