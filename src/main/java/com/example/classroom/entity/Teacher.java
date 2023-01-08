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
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "teachers")
public class Teacher {

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

    @JsonIgnore
    @OneToOne(mappedBy = "dean")
    private Department departmentDean;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH})
    @JoinTable(name = "teacher_subjects",
            joinColumns = { @JoinColumn(name = "teacher_id") },
            inverseJoinColumns = { @JoinColumn(name = "subject_id") })
    private Set<Subject> subjects = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH})
    @JoinTable(name = "teacher_students",
            joinColumns = { @JoinColumn(name = "teacher_id") },
            inverseJoinColumns = { @JoinColumn(name = "student_id") })
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student){
        this.students.add(student);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
    }

    public void addSubject(Subject subject){
        this.subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return age == teacher.age && id.equals(teacher.id) && firstName.equals(teacher.firstName) && lastName.equals(teacher.lastName) && email.equals(teacher.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, email);
    }
}
