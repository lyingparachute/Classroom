package systems.ultimate.classroom.service;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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

    @Autowired
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }


    @Test
    void create_shouldSaveTeacher_givenTeacherDto() {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        ArrayList<Student> students = new ArrayList<>(List.of(student1, student2));
        TeacherDto expected = new TeacherDto();
        expected.setFirstName("Fabian");
        expected.setLastName("Graczyk");
        expected.setEmail("f.graczyk@gmail.com");
        expected.setAge(55);
        expected.setSubject(Subject.MATHS);
        expected.setStudentsList(new HashSet<>(students));
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
        assertThat(actual.getSubject()).isEqualTo(expected.getSubject());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()));
    }

    @Test
    void update_shouldUpdateTeacher_givenTeacherDto() {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Teacher teacher = initData.createTeacherTwo(List.of());
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setFirstName("Fabian");
        dto.setLastName("Graczyk");
        dto.setEmail("f.graczyk@gmail.com");
        dto.setAge(55);
        dto.setSubject(Subject.MATHS);
        dto.setStudentsList(new HashSet<>(List.of(student1, student2)));
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
        assertThat(actual.getSubject()).isEqualTo(dto.getSubject());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()));
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
        dto.setSubject(Subject.MATHS);
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
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher1 = initData.createTeacherOne(List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(List.of(student1, student2, student3));
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
        assertThat(actualTeacher1.getSubject()).isEqualTo(teacher1.getSubject());
        assertThat(actualTeacher1.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()));
        assertThat(actualTeacher2.getFirstName()).isEqualTo(teacher2.getFirstName());
        assertThat(actualTeacher2.getLastName()).isEqualTo(teacher2.getLastName());
        assertThat(actualTeacher2.getEmail()).isEqualTo(teacher2.getEmail());
        assertThat(actualTeacher2.getAge()).isEqualTo(teacher2.getAge());
        assertThat(actualTeacher2.getSubject()).isEqualTo(teacher2.getSubject());
        assertThat(actualTeacher2.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge(), student3.getFieldOfStudy()));
        assertThat(actualTeacher3.getFirstName()).isEqualTo(teacher3.getFirstName());
        assertThat(actualTeacher3.getLastName()).isEqualTo(teacher3.getLastName());
        assertThat(actualTeacher3.getEmail()).isEqualTo(teacher3.getEmail());
        assertThat(actualTeacher3.getAge()).isEqualTo(teacher3.getAge());
        assertThat(actualTeacher3.getSubject()).isEqualTo(teacher3.getSubject());
        assertThat(actualTeacher3.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge(), student3.getFieldOfStudy()));
    }

    @Test
    void fetchAllPaginated_shouldReturnAllTeachersPaginated_givenPageNo_PageSize_SortDir() {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher1 = initData.createTeacherOne(List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(List.of(student1, student2, student3));
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
        assertThat(actualTeacher.getSubject()).isEqualTo(teacher3.getSubject());
        assertThat(actualTeacher.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge(), student3.getFieldOfStudy()));
    }

    @Test
    void fetchById_shouldFindStudent_givenId() {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher = initData.createTeacherThree(List.of(student1, student2, student3));
        //when
        TeacherDto actual = teacherService.fetchById(teacher.getId());
        //then
        assertThat(actual.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacher.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacher.getSubject());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy()),
                        Tuple.tuple(student3.getId(), student3.getFirstName(), student3.getLastName(),
                                student3.getEmail(), student3.getAge(), student3.getFieldOfStudy()));
    }

    @Test
    void remove() {
    }

    @Test
    void findByFirstOrLastName() {
    }

    @Test
    void assignStudents() {
    }
}