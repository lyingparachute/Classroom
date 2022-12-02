package systems.ultimate.classroom.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import systems.ultimate.classroom.enums.FieldOfStudy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Student {

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
    private FieldOfStudy fieldOfStudy;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.DETACH
            },
            mappedBy = "studentsList")
    private Set<Teacher> teachersList = new HashSet<>();

    public void removeTeacher(Teacher teacher){
        this.teachersList.remove(teacher);
        teacher.getStudentsList().remove(this);
    }

    public void assignTeacher(Teacher teacher){
        this.teachersList.add(teacher);
        teacher.getStudentsList().add(this);
    }
}
