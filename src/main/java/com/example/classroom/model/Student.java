package com.example.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    @Column(unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
    private User userDetails;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH})
    @JoinColumn(name = "field_id")
    private FieldOfStudy fieldOfStudy;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private Set<Teacher> teachers = new HashSet<>();

    public Set<Teacher> getTeachers() {
        return new HashSet<>(teachers);
    }

    /**
     * Set new Student's fieldOfStudy. The method keeps
     * relationships consistency:
     * * this Student is removed from the previous FieldOfStudy
     * * this Student is added to next FieldOfStudy
     */
    public void setFieldOfStudy(FieldOfStudy fieldOfStudy) {
        if (sameAsFormer(fieldOfStudy))
            return;
        FieldOfStudy oldFieldOfStudy = this.fieldOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        if (oldFieldOfStudy != null)
            oldFieldOfStudy.removeStudent(this);
        if (fieldOfStudy != null) {
            fieldOfStudy.addStudent(this);
        }
    }

    private boolean sameAsFormer(FieldOfStudy newFieldOfStudy) {
        if (fieldOfStudy == null)
            return newFieldOfStudy == null;
        return fieldOfStudy.equals(newFieldOfStudy);
    }

    /**
     * Add new Teacher. The method keeps relationships consistency:
     * * this student is added to students
     * on the teacher side
     */
    public void addTeacher(Teacher teacher) {
        teacher.addStudent(this);
        if (teachers.contains(teacher)) {
            return;
        }
        teachers.add(teacher);
    }

    /**
     * Removes the Teacher. The method keeps relationships consistency:
     * * this student is removed from students
     * on the teacher side
     */
    public void removeTeacher(Teacher teacher) {
        teacher.removeStudent(this);
        if (!teachers.contains(teacher)) {
            return;
        }
        teachers.remove(teacher);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && id.equals(student.id) && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(email, student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, email);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
