package com.example.classroom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    private String telNumber;

    @JsonIgnore
    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dean_id")
    private Teacher dean;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "department")
    private Set<FieldOfStudy> fieldsOfStudy = new HashSet<>();

    public Set<FieldOfStudy> getFieldsOfStudy() {
        //defensive copy, nobody will be able to change Set from the outside
        return new HashSet<>(fieldsOfStudy);
    }

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
            oldDean.setDepartment(null);
        if (dean != null)
            dean.setDepartment(this);
    }

    private boolean sameAsFormer(Teacher newDean) {
        if (dean == null)
            return newDean == null;
        return dean.equals(newDean);
    }

    /**
     * Add new Field Of Study. The method keeps relationships consistency:
     * * this Department is added to departments
     * on the Field Of Study side
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
     * on the Field Of Study side
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
