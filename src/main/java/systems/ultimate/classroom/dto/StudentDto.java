package systems.ultimate.classroom.dto;

import lombok.Data;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;

import java.io.Serializable;
import java.util.List;

@Data
public class StudentDto implements Serializable {
    private final Long id;
    private final String FirstName;
    private final String LastName;
    private final int age;
    private final String email;
    private final FieldOfStudy fieldOfStudy;
    private List<Teacher> teachersList;
}
