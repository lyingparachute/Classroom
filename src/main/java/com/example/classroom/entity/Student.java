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

    public void removeTeacher(Teacher teacher){
        this.teachers.remove(teacher);
        teacher.getStudents().remove(this);
    }

    public void assignTeacher(Teacher teacher){
        this.teachers.add(teacher);
        teacher.getStudents().add(this);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
