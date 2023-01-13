package com.example.classroom.entity;

import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "fields_of_study")
public class FieldOfStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LevelOfEducation levelOfEducation;

    @Column(name = "study_mode")
    @Enumerated(EnumType.STRING)
    private ModeOfStudy mode;

    @Column(name = "obtained_title")
    @Enumerated(EnumType.STRING)
    private AcademicTitle title;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.REFRESH,
                    CascadeType.MERGE,
                    CascadeType.DETACH})
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    private Department department;

    @OneToMany(mappedBy = "fieldOfStudy",
            cascade = {
                    CascadeType.MERGE}
    )
    @ToString.Exclude
    private Set<Subject> subjects = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fieldOfStudy")
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    /**
     * Set new Field Of Study's department. The method keeps
     * relationships consistency:
     * * this Field Of Study is removed from the previous Department
     * * this Field Of Study is added to next Department
     */
    public void setDepartment(Department department) {
        if (sameAsFormer(department))
            return;
        Department oldDepartment = this.department;
        this.department = department;
        if (oldDepartment != null)
            oldDepartment.removeFieldOfStudy(this);
        if (department != null) {
            department.addFieldOfStudy(this);
        }
    }

    private boolean sameAsFormer(Department newDepartment) {
        if (department == null)
            return newDepartment == null;
        return department.equals(newDepartment);
    }

    /**
     * Add new Subject. The method keeps relationships consistency:
     * * this teacher is added to teachers
     *   on the subject side
     */
    public void addSubject(Subject subject){
        //prevent endless loop
        if (subjects.contains(subject)) {
            return;
        }
        subjects.add(subject);
        subject.setFieldOfStudy(this);
    }

    /**
     * Remove Subject. The method keeps relationships consistency:
     * * this teacher is removed from teachers
     *   on the subject side
     */
    public void removeSubject(Subject subject) {
        //prevent endless loop
        if (!subjects.contains(subject)) {
            return;
        }
        subjects.remove(subject);
        subject.setFieldOfStudy(null);
    }

    /**
     * Add new Student. The method keeps relationships consistency:
     * * this teacher is added to teachers
     *   on the student side
     */
    public void addStudent(Student student){
        //prevent endless loop
        if (students.contains(student)) {
            return;
        }
        students.add(student);
        student.setFieldOfStudy(this);
    }

    /**
     * Remove Student. The method keeps relationships consistency:
     * * this teacher is removed from teachers
     *   on the student side
     */
    public void removeStudent(Student student) {
        //prevent endless loop
        if (!students.contains(student)) {
            return;
        }
        students.remove(student);
        student.setFieldOfStudy(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldOfStudy that = (FieldOfStudy) o;
        return id.equals(that.id) && name.equals(that.name) && levelOfEducation == that.levelOfEducation && mode == that.mode && title == that.title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, levelOfEducation, mode, title);
    }
}
