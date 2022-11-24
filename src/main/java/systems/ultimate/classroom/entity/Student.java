package systems.ultimate.classroom.entity;

import systems.ultimate.classroom.enums.FieldOfStudy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String FirstName;

    private String LastName;

    private int age;

    private String email;

    private FieldOfStudy fieldOfStudy;

    @ManyToMany
    private List<Teacher> teachersList;
}
