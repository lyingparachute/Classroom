package com.example.classroom.entity;

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
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String address;

//    @Pattern(regexp = "(\\+48|0)[0-9]{9}")
    private int telNumber;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "dean_id")
    private Teacher dean;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "department")
    private Set<FieldOfStudy> fieldsOfStudy = new HashSet<>();

    /**
     * Set new Department's dean(Teacher). The method keeps
     * relationships consistency:
     * * this Department is removed from the previous dean(Teacher)
     * * this Department is added to next dean(Teacher)
     */
    public void setDean(Teacher dean) {
        if (sameAsFormer(dean))
            return;
        Teacher oldDean = this.dean;
        this.dean = dean;
        if (oldDean != null)
            oldDean.setDepartmentDean(null);
        if (dean != null)
            dean.setDepartmentDean(this);
    }

    private boolean sameAsFormer(Teacher newDean) {
        if (dean == null)
            return newDean == null;
        return dean.equals(newDean);
    }

    /**
     * Add new Field Of Study. The method keeps relationships consistency:
     * * this Department is added to departments
     *   on the Field Of Study side
     */
    public void addFieldOfStudy(FieldOfStudy fieldOfStudy) {
        //prevent endless loop
        if (fieldsOfStudy.contains(fieldOfStudy))
            return;
        fieldsOfStudy.add(fieldOfStudy);
        fieldOfStudy.setDepartment(this);
    }

    /**
     * Remove Teacher. The method keeps relationships consistency:
     * * this Department is removed from departments
     *   on the Field Of Study side
     */
    public void removeFieldOfStudy(FieldOfStudy fieldOfStudy) {
        //prevent endless loop
        if (!fieldsOfStudy.contains(fieldOfStudy))
            return;
        fieldsOfStudy.remove(fieldOfStudy);
        fieldOfStudy.setDepartment(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return telNumber == that.telNumber && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, telNumber);
    }
}
