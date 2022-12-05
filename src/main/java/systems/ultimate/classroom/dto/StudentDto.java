package systems.ultimate.classroom.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

//@Data
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StudentDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private FieldOfStudy fieldOfStudy;
//    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachersList = new HashSet<>();

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
