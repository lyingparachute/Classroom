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

    @ManyToOne
    @JoinColumn(name = "field_id")
    private FieldOfStudy fieldOfStudy;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "students")
    private Set<Teacher> teachers = new HashSet<>();

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
