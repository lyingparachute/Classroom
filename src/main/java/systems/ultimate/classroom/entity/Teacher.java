package systems.ultimate.classroom.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import systems.ultimate.classroom.enums.Subject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH})
    @JoinTable(name = "teacher_students",
            joinColumns = { @JoinColumn(name = "teacher_id") },
            inverseJoinColumns = { @JoinColumn(name = "student_id") })
    private Set<Student> studentsList = new HashSet<>();

    public void addStudent(Student student){
        this.studentsList.add(student);
    }
}
