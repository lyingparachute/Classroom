package systems.ultimate.classroom.dto;

import lombok.Data;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.enums.Subject;

import java.io.Serializable;
import java.util.List;

@Data
public class TeacherDto implements Serializable {
    private final Long id;
    private final String FirstName;
    private final String LastName;
    private final int age;
    private final String email;
    private final Subject subject;
    private List<Student> studentsList;
}
