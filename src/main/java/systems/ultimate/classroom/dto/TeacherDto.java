package systems.ultimate.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.enums.Subject;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private Subject subject;
    private Set<Student> studentsList;

    @Override
    public String toString() {
        return firstName + " " + lastName +
                ", subject=" + subject.getDesc();
    }
}
