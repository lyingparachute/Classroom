package com.example.classroom.entity;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Length(max = 500)
    private String description;

    @PositiveOrZero
    private int hoursInSemester;

    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH
            },
            mappedBy = "subjects")
    private Set<Teacher> teachers = new HashSet<>();


    public void removeTeacher(Teacher teacher){
        this.teachers.remove(teacher);
        teacher.getSubjects().remove(this);
    }

    public void assignTeacher(Teacher teacher){
        this.teachers.add(teacher);
        teacher.getSubjects().add(this);
    }

    @Override
    public String toString() {
        return  "id=" + id + ", name='" + name;
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
