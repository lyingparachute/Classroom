package com.example.classroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
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

    @NotNull
    @NotEmpty(message = "{message.empty}")
    @Length(min = 2, max = 60, message = "{message.name}")
    private String firstName;

    @NotNull
    @NotEmpty(message = "{message.empty}")
    @Length(min = 2, max = 60, message = "{message.last.name}")
    private String lastName;

    @NotNull
    @Min(value = 18, message = "{message.min.age}")
    private int age;

    @NotNull
    @NotEmpty(message = "{message.empty}")
    @Email(message = "{message.valid.email}")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH})
    @JoinColumn(name = "field_id")
    private FieldOfStudy fieldOfStudy;

    @JsonIgnore
    @ManyToMany(mappedBy = "students")
    private Set<Teacher> teachers = new HashSet<>();

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
     *   on the teacher side
     */
    public void addTeacher(Teacher teacher){
        //prevent endless loop
        if (teachers.contains(teacher)){
            return;
        }
        teachers.add(teacher);
        teacher.addStudent(this);
    }

    /**
     * Removes the Teacher. The method keeps relationships consistency:
     * * this student is removed from students
     *   on the teacher side
     */
    public void removeTeacher(Teacher teacher){
        //prevent endless loop
        if (!teachers.contains(teacher)){
            return;
        }
        teachers.remove(teacher);
        teacher.removeStudent(this);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
