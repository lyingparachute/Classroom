package systems.ultimate.classroom.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import systems.ultimate.classroom.enums.FieldOfStudy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
