package systems.ultimate.classroom.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.enums.Subject;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TeacherDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private Subject subject;
    private Set<Student> studentsList = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", subject=" + subject.getDesc();
    }
}
