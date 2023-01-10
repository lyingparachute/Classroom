package com.example.classroom.service;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.InitData;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ActiveProfiles("test")
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
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void create_shouldSaveStudent_givenStudentDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        ArrayList<Teacher> teachers = new ArrayList<>(List.of(teacher1, teacher2));
        StudentDto expected = new StudentDto();
        expected.setFirstName("Fabian");
        expected.setLastName("Graczyk");
        expected.setEmail("f.graczyk@gmail.com");
        expected.setAge(22);
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
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void update_shouldUpdateStudent_givenStudentDto() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Student student = initData.createStudentOne(null, List.of());
        StudentDto expected = new StudentDto();
        expected.setId(student.getId());
        expected.setFirstName("Pamela");
        expected.setLastName("Gonzales");
        expected.setEmail("p.gonzales@gmail.com");
        expected.setAge(20);
        expected.setTeachersList(new HashSet<>(List.of(teacher1, teacher2)));
        //when
        studentService.update(expected);
        //then
        Optional<Student> byId = studentRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(expected.getFieldOfStudy());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void update_throwsIllegalArgumentException_givenWrongStudentDto() {
        //given
        StudentDto dto = new StudentDto();
        dto.setId(1L);
        dto.setFirstName("Alison");
        dto.setLastName("Becker");
        dto.setEmail("a.becker@gmail.com");
        dto.setAge(29);
        //when
        Throwable thrown = catchThrowable(() -> studentService.update(dto));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid student '" + dto + "' with ID: " + dto.getId());
    }

    @Test
    void fetchAll_shouldReturnAllStudents() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
        Student student1 = initData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(null, List.of(teacher3));
        Student student3 = initData.createStudentThree(null, List.of(teacher1, teacher2, teacher3));
        //when
        List<StudentDto> actual = studentService.fetchAll();
        //then
        assertThat(actual).size().isEqualTo(3);
        StudentDto actualStudent1 = actual.get(0);
        StudentDto actualStudent2 = actual.get(1);
        StudentDto actualStudent3 = actual.get(2);

        assertThat(actualStudent1.getFirstName()).isEqualTo(student1.getFirstName());
        assertThat(actualStudent1.getLastName()).isEqualTo(student1.getLastName());
        assertThat(actualStudent1.getEmail()).isEqualTo(student1.getEmail());
        assertThat(actualStudent1.getAge()).isEqualTo(student1.getAge());
        Assertions.assertThat(actualStudent1.getFieldOfStudy()).isEqualTo(student1.getFieldOfStudy());
        Assertions.assertThat(actualStudent1.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        assertThat(actualStudent2.getFirstName()).isEqualTo(student2.getFirstName());
        assertThat(actualStudent2.getLastName()).isEqualTo(student2.getLastName());
        assertThat(actualStudent2.getEmail()).isEqualTo(student2.getEmail());
        assertThat(actualStudent2.getAge()).isEqualTo(student2.getAge());
        Assertions.assertThat(actualStudent2.getFieldOfStudy()).isEqualTo(student2.getFieldOfStudy());
        Assertions.assertThat(actualStudent2.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
        assertThat(actualStudent3.getFirstName()).isEqualTo(student3.getFirstName());
        assertThat(actualStudent3.getLastName()).isEqualTo(student3.getLastName());
        assertThat(actualStudent3.getEmail()).isEqualTo(student3.getEmail());
        assertThat(actualStudent3.getAge()).isEqualTo(student3.getAge());
        Assertions.assertThat(actualStudent3.getFieldOfStudy()).isEqualTo(student3.getFieldOfStudy());
        Assertions.assertThat(actualStudent3.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllStudentsPaginated_givenPageNo_PageSize_SortDir() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(null, List.of(teacher3));
        Student student3 = initData.createStudentThree(null, List.of(teacher1, teacher2, teacher3));
        int pageNo = 2;
        int pageSize = 2;
        String sortField = "firstName";
        String sortDirection = Sort.Direction.DESC.name();
        //when
        Page<StudentDto> actual = studentService.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        List<StudentDto> actualContent = actual.getContent();
        assertThat(actualContent).size().isEqualTo(1);
        StudentDto actualStudent = actualContent.get(0);
        assertThat(actualStudent.getFirstName()).isEqualTo(student3.getFirstName());
        assertThat(actualStudent.getLastName()).isEqualTo(student3.getLastName());
        assertThat(actualStudent.getEmail()).isEqualTo(student3.getEmail());
        assertThat(actualStudent.getAge()).isEqualTo(student3.getAge());
        Assertions.assertThat(actualStudent.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void fetchById_shouldFindStudent_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());

        Student student = initData.createStudentOne(null, List.of(teacher1, teacher2, teacher3));
        //when
        StudentDto actual = studentService.fetchById(student.getId());
        //then
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
        Assertions.assertThat(actual.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void fetchById_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> studentService.fetchById(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid student id: " + id);
    }

    @Test
    void remove_shouldRemoveStudent_givenId() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());

        Student student = initData.createStudentOne(null, List.of(teacher1, teacher2, teacher3));
        //when
        studentService.remove(student.getId());
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isNotPresent();
        assertThat(teacherRepository.findById(teacher1.getId())).isPresent();
        assertThat(teacherRepository.findById(teacher2.getId())).isPresent();
        assertThat(teacherRepository.findById(teacher3.getId())).isPresent();
    }

    @Test
    void remove_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> studentService.remove(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid student id: " + id);
    }

    @Test
    void findByFirstOrLastName_returnsStudentsSearchedByFirstOrLastName_givenName() {
        //given
        String name = "w";
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(null, List.of(teacher3));
        Student student3 = initData.createStudentThree(null, List.of(teacher1, teacher2, teacher3));
        //when
        List<StudentDto> actual = studentService.findByFirstOrLastName(name);
        //then
        assertThat(actual.size()).isEqualTo(2);
        StudentDto actualStudent1 = actual.get(0);
        StudentDto actualStudent2 = actual.get(1);

        assertThat(actualStudent1.getFirstName()).isEqualTo(student2.getFirstName());
        assertThat(actualStudent1.getLastName()).isEqualTo(student2.getLastName());
        assertThat(actualStudent1.getEmail()).isEqualTo(student2.getEmail());
        assertThat(actualStudent1.getAge()).isEqualTo(student2.getAge());
        assertThat(actualStudent1.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
        assertThat(actualStudent2.getFirstName()).isEqualTo(student3.getFirstName());
        assertThat(actualStudent2.getLastName()).isEqualTo(student3.getLastName());
        assertThat(actualStudent2.getEmail()).isEqualTo(student3.getEmail());
        assertThat(actualStudent2.getAge()).isEqualTo(student3.getAge());
        assertThat(actualStudent2.getTeachersList())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()),
                        Tuple.tuple(teacher3.getId(), teacher3.getFirstName(), teacher3.getLastName(),
                                teacher3.getEmail(), teacher3.getAge()));
    }

    @Test
    void assignTeachers_shouldAssignTeachersToStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        HashSet<Teacher> teachers = new HashSet<>(List.of(teacher1, teacher2));
        Student student = initData.createStudentOne(null, List.of());
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
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }

    @Test
    void removeTeachers_shouldRemoveTeachersFromStudent_givenTeachersSet() {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
        Student student = initData.createStudentOne(null, List.of(teacher1, teacher2));
        Optional<Student> byId2 = studentRepository.findById(student.getId());
        assertThat(byId2).isPresent();
        Student actual2 = byId2.get();
        assertThat(actual2.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).contains(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
        //when
        studentService.removeTeachers(student, new HashSet<>(student.getTeachers()));
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(student.getLastName());
        assertThat(actual.getEmail()).isEqualTo(student.getEmail());
        assertThat(actual.getAge()).isEqualTo(student.getAge());
        assertThat(actual.getTeachers())
                .extracting(
                        Teacher::getId,
                        Teacher::getFirstName,
                        Teacher::getLastName,
                        Teacher::getEmail,
                        Teacher::getAge
                ).doesNotContain(
                        Tuple.tuple(teacher1.getId(), teacher1.getFirstName(), teacher1.getLastName(),
                                teacher1.getEmail(), teacher1.getAge()),
                        Tuple.tuple(teacher2.getId(), teacher2.getFirstName(), teacher2.getLastName(),
                                teacher2.getEmail(), teacher2.getAge()));
    }
}