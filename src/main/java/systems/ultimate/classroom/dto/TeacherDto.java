package systems.ultimate.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.enums.Subject;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDto implements Serializable {
    private Long id;
    private String FirstName;
    private String LastName;
    private int age;
    private String email;
    private Subject subject;
    private List<Student> studentsList;

    @Override
    public String toString() {
        return FirstName + " " + LastName +
                ", subject=" + subject.description();
    }
}
