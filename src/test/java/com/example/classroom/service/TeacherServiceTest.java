package com.example.classroom.service;

import com.example.classroom.dto.TeacherDto;
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
class TeacherServiceTest {

    @Autowired
    private InitData initData;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }


    @Test
    void create_shouldSaveTeacher_givenTeacherDto() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        ArrayList<Student> students = new ArrayList<>(List.of(student1, student2));
        TeacherDto expected = new TeacherDto();
        expected.setFirstName("Fabian");
        expected.setLastName("Graczyk");
        expected.setEmail("f.graczyk@gmail.com");
        expected.setAge(55);
        expected.setStudents(new HashSet<>(students));
        //when
        teacherService.create(expected);
        //then
        Optional<Teacher> byId = teacherRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Teacher actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        Assertions.assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()));
    }

    @Test
    void update_shouldUpdateTeacher_givenTeacherDto() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher teacher = initData.createTeacherTwo(null, List.of(), List.of());
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setFirstName("Fabian");
        dto.setLastName("Graczyk");
        dto.setEmail("f.graczyk@gmail.com");
        dto.setAge(55);
        dto.setStudents(new HashSet<>(List.of(student1, student2)));
        //when
        teacherService.update(dto);
        //then
        Optional<Teacher> byId = teacherRepository.findById(dto.getId());
        assertThat(byId).isPresent();
        Teacher actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(dto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(dto.getEmail());
        assertThat(actual.getAge()).isEqualTo(dto.getAge());
        Assertions.assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()));
    }

    @Test
    void update_throwsIllegalArgumentException_givenWrongTeacherDto() {
        //given
        TeacherDto dto = new TeacherDto();
        dto.setId(9L);
        dto.setFirstName("Alison");
        dto.setLastName("Becker");
        dto.setEmail("a.becker@gmail.com");
        dto.setAge(55);
        //when
        Throwable thrown = catchThrowable(() -> teacherService.update(dto));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid teacher '" + dto.getFirstName() + " " + dto.getLastName() + "' with ID: " + dto.getId());
    }

    @Test
    void fetchAll_shouldReturnAllTeachers() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        //when
        List<TeacherDto> actual = teacherService.fetchAll();
        //then
        assertThat(actual).size().isEqualTo(3);
        TeacherDto actualTeacher1 = actual.get(0);
        TeacherDto actualTeacher2 = actual.get(1);
        TeacherDto actualTeacher3 = actual.get(2);
        assertThat(actualTeacher1.getFirstName()).isEqualTo(teacher1.getFirstName());
        assertThat(actualTeacher1.getLastName()).isEqualTo(teacher1.getLastName());
        assertThat(actualTeacher1.getEmail()).isEqualTo(teacher1.getEmail());
        assertThat(actualTeacher1.getAge()).isEqualTo(teacher1.getAge());
        assertThat(actualTeacher1.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()));
        assertThat(actualTeacher2.getFirstName()).isEqualTo(teacher2.getFirstName());
        assertThat(actualTeacher2.getLastName()).isEqualTo(teacher2.getLastName());
        assertThat(actualTeacher2.getEmail()).isEqualTo(teacher2.getEmail());
        assertThat(actualTeacher2.getAge()).isEqualTo(teacher2.getAge());
        assertThat(actualTeacher2.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
        assertThat(actualTeacher3.getFirstName()).isEqualTo(teacher3.getFirstName());
        assertThat(actualTeacher3.getLastName()).isEqualTo(teacher3.getLastName());
        assertThat(actualTeacher3.getEmail()).isEqualTo(teacher3.getEmail());
        assertThat(actualTeacher3.getAge()).isEqualTo(teacher3.getAge());
        assertThat(actualTeacher3.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllTeachersPaginated_givenPageNo_PageSize_SortDir() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        int pageNo = 2;
        int pageSize = 2;
        String sortField = "firstName";
        String sortDirection = Sort.Direction.DESC.name();
        //when
        Page<TeacherDto> actual = teacherService.fetchAllPaginated(pageNo, pageSize, sortField, sortDirection);
        //then
        List<TeacherDto> actualContent = actual.getContent();
        assertThat(actualContent).size().isEqualTo(1);
        TeacherDto actualTeacher = actualContent.get(0);
        assertThat(actualTeacher.getFirstName()).isEqualTo(teacher3.getFirstName());
        assertThat(actualTeacher.getLastName()).isEqualTo(teacher3.getLastName());
        assertThat(actualTeacher.getEmail()).isEqualTo(teacher3.getEmail());
        assertThat(actualTeacher.getAge()).isEqualTo(teacher3.getAge());
        assertThat(actualTeacher.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
    }

    @Test
    void fetchById_shouldFindStudent_givenId() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        Teacher teacher = initData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        //when
        TeacherDto actual = teacherService.fetchById(teacher.getId());
        //then
        assertThat(actual.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacher.getAge());
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
    }

    @Test
    void fetchById_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> teacherService.fetchById(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid teacher ID: " + id);
    }


    @Test
    void remove_shouldRemoveStudent_givenId() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        Teacher teacher = initData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));
        //when
        teacherService.remove(teacher.getId());
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
        assertThat(studentRepository.findById(student1.getId())).isPresent();
        assertThat(studentRepository.findById(student2.getId())).isPresent();
        assertThat(studentRepository.findById(student3.getId())).isPresent();
    }

    @Test
    void remove_throwsIllegalArgumentException_givenWrongId() {
        //given
        Long id = 1L;
        //when
        Throwable thrown = catchThrowable(() -> teacherService.remove(id));
        //then
        assertThat(thrown)
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid teacher ID: " + id);
    }

    @Test
    void findByFirstOrLastName_returnsTeachersSearchedByFirstOrLastName_givenName() {
        //given
        String name = "ja";
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of(student1, student2, student3));

        //when
        List<TeacherDto> actual = teacherService.findByFirstOrLastName(name);
        //then
        assertThat(actual.size()).isEqualTo(2);
        TeacherDto actualTeacher1 = actual.get(0);
        TeacherDto actualTeacher2 = actual.get(1);
        assertThat(actualTeacher1.getFirstName()).isEqualTo(teacher1.getFirstName());
        assertThat(actualTeacher1.getLastName()).isEqualTo(teacher1.getLastName());
        assertThat(actualTeacher1.getEmail()).isEqualTo(teacher1.getEmail());
        assertThat(actualTeacher1.getAge()).isEqualTo(teacher1.getAge());
        assertThat(actualTeacher1.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()));
        assertThat(actualTeacher2.getFirstName()).isEqualTo(teacher2.getFirstName());
        assertThat(actualTeacher2.getLastName()).isEqualTo(teacher2.getLastName());
        assertThat(actualTeacher2.getEmail()).isEqualTo(teacher2.getEmail());
        assertThat(actualTeacher2.getAge()).isEqualTo(teacher2.getAge());
        assertThat(actualTeacher2.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
    }

    @Test
    void assignStudents_shouldAssignStudentsToTeacher_givenStudentsSet() {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());
        Student student3 = initData.createStudentThree(null, List.of());

        HashSet<Student> students = new HashSet<>(List.of(student1, student2, student3));
        Teacher teacher = initData.createTeacherOne(null, List.of(), List.of());
        //when
        teacherService.assignStudents(teacher, students);
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isPresent();
        Teacher actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacher.getAge());
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge()));
    }
}