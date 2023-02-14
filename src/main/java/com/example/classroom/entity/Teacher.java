package com.example.classroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
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
    private String firstName;
    private String lastName;
    private int age;

    @NotNull
    @NotEmpty(message = "{message.empty}")
    @Email(message = "{message.email.valid}")
    private String email;

    @JsonIgnore
    @OneToOne(mappedBy = "dean")
    private Department department;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH})
    @JoinTable(name = "teacher_subjects",
            joinColumns = {@JoinColumn(name = "teacher_id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id")})
    private Set<Subject> subjects = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.DETACH})
    @JoinTable(name = "teacher_students",
            joinColumns = {@JoinColumn(name = "teacher_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")})
    private Set<Student> students = new HashSet<>();

    public Set<Subject> getSubjects() {
        //defensive copy, nobody will be able to change Set from the outside
        return new HashSet<>(subjects);
    }

    public Set<Student> getStudents() {
        //defensive copy, nobody will be able to change Set from the outside
        return new HashSet<>(students);
    }

    public void setDepartment(Department department) {
        if (sameAsFormer(department))
            return;
        Department oldDepartmentDean = this.department;
        this.department = department;
        if (oldDepartmentDean != null)
            oldDepartmentDean.setDean(null);
        if (department != null) {
            department.setDean(this);
        }
    }

    private boolean sameAsFormer(Department newDepartmentDean) {
        if (department == null)
            return newDepartmentDean == null;
        return department.equals(newDepartmentDean);
    }

    /**
     * Add new Student. The method keeps relationships consistency:
     * * this teacher is added to teachers
     * on the student side
     */
    public void addStudent(Student student) {
        //prevent endless loop
        if (students.contains(student)) {
            return;
        }
        students.add(student);
        student.addTeacher(this);
    }

    /**
     * Remove Student. The method keeps relationships consistency:
     * * this teacher is removed from teachers
     * on the student side
     */
    public void removeStudent(Student student) {
        //prevent endless loop
        if (!students.contains(student)) {
            return;
        }
        students.remove(student);
        student.removeTeacher(this);
    }

    /**
     * Add new Subject. The method keeps relationships consistency:
     * * this teacher is added to teachers
     * on the subject side
     */
    public void addSubject(Subject subject) {
        //prevent endless loop
        if (subjects.contains(subject)) {
            return;
        }
        subjects.add(subject);
        subject.addTeacher(this);
    }

    /**
     * Remove Subject. The method keeps relationships consistency:
     * * this teacher is removed from teachers
     * on the subject side
     */
    public void removeSubject(Subject subject) {
        //prevent endless loop
        if (!subjects.contains(subject)) {
            return;
        }
        subjects.remove(subject);
        subject.removeTeacher(this);
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
