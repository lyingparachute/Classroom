package systems.ultimate.classroom.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import systems.ultimate.classroom.dto.StudentDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.FieldOfStudy;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.util.InitData;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudentServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldSaveStudent_givenStudentDto_returnsStudentDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        ArrayList<Teacher> teachers = new ArrayList<>(List.of(teacher1, teacher2));
        StudentDto expected = new StudentDto();
        expected.setFirstName("Fabian");
        expected.setLastName("Graczyk");
        expected.setEmail("f.graczyk@gmail.com");
        expected.setAge(22);
        expected.setFieldOfStudy(FieldOfStudy.INFORMATICS);
        expected.setTeachersList(new HashSet<>(teachers));
        //when
        studentService.create(expected);
        //then
        Optional<Student> byId = studentRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy());
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }

    @Test
    void shouldUpdateStudent_GivenStudentDto_returnsStudentDto() {

    }

    @Test
    void shouldReturnAllStudents() {

    }

    @Test
    void shouldReturnAllStudentsPaginated() {

    }

    @Test
    void shouldFindStudent_givenId_returnsStudentDto() {

    }

    @Test
    void shouldRemoveStudent_givenId() {

    }

    @Test
    void shouldFindStudentsByFirstOrLastName_givenName() {
        //given
        String name = "w";
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Teacher teacher3 = initData.createTeacherThree(List.of());

        Student student1 = initData.createStudentOne(List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(List.of(teacher3));
        Student student3 = initData.createStudentThree(List.of(teacher1, teacher2, teacher3));

        StudentDto dto1 = mapper.map(student1, StudentDto.class);
        StudentDto dto2 = mapper.map(student2, StudentDto.class);
        StudentDto dto3 = mapper.map(student3, StudentDto.class);
        //when
        List<StudentDto> actual = studentService.findByFirstOrLastName(name);
        //then
        assertThat(actual.size()).isEqualTo(2);
        StudentDto actualStudent1 = actual.get(0);
        StudentDto actualStudent2 = actual.get(1);

        assertThat(actualStudent1.getFirstName()).isEqualTo(dto2.getFirstName());
        assertThat(actualStudent1.getLastName()).isEqualTo(dto2.getLastName());
        assertThat(actualStudent1.getEmail()).isEqualTo(dto2.getEmail());
        assertThat(actualStudent1.getAge()).isEqualTo(dto2.getAge());
        assertThat(actualStudent1.getFieldOfStudy()).isEqualTo(dto2.getFieldOfStudy());
        assertThat(actualStudent1.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher3.getSubject()));
        assertThat(actualStudent2.getFirstName()).isEqualTo(dto3.getFirstName());
        assertThat(actualStudent2.getLastName()).isEqualTo(dto3.getLastName());
        assertThat(actualStudent2.getEmail()).isEqualTo(dto3.getEmail());
        assertThat(actualStudent2.getAge()).isEqualTo(dto3.getAge());
        assertThat(actualStudent2.getFieldOfStudy()).isEqualTo(dto3.getFieldOfStudy());
        assertThat(actualStudent2.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge(), teacher3.getSubject()));
    }

    @Test
    void shouldAssignTeachersToStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        HashSet<Teacher> teachers = new HashSet<>(List.of(teacher1, teacher2));
        Student student = initData.createStudentOne(List.of());
        //when
        studentService.assignTeachers(student, teachers);
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(student.getFieldOfStudy());
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }

    @Test
    void shouldRemoveTeachersFromStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student = initData.createStudentOne(List.of(teacher1, teacher2));
        Optional<Student> byId2 = studentRepository.findById(student.getId());
        assertThat(byId2).isPresent();
        Student actual2 = byId2.get();
        assertThat(actual2.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).contains(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
        //when
        studentService.removeTeachers(student, new HashSet<>(student.getTeachersList()));
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(student.getFieldOfStudy());
        assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge,
                        Teacher::getSubject
                ).doesNotContain(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge(), teacher1.getSubject()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge(), teacher2.getSubject()));
    }
}