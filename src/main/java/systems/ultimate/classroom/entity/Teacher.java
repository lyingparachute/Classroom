package systems.ultimate.classroom.entity;

import systems.ultimate.classroom.enums.Subject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Teacher {

    @Id
    private Long id;

    private String FirstName;

    private String LastName;

    private int age;

    private String email;

    private Subject subject;

    @ManyToMany
    private List<Student> studentsList;
}
