package systems.ultimate.classroom.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
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
        student.getTeachersList().add(this);
    }

    public void removeStudent(Student student) {
        this.studentsList.remove(student);
    }
}
