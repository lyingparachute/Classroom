package systems.ultimate.classroom.dto;

import lombok.*;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto implements Serializable {
    private Long id;
    private String FirstName;
    private String LastName;
    private int age;
    private String email;
    private FieldOfStudy fieldOfStudy;
    private List<Teacher> teachersList;

}
