package systems.ultimate.classroom.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import systems.ultimate.classroom.enums.Subject;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Teacher {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private int age;

    private String email;

    @Enumerated(EnumType.STRING)
    private Subject subject;

    @ManyToMany
    private List<Student> studentsList;
}
