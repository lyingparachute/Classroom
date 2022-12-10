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
    void update() {
    }

    @Test
    void fetchAll() {
    }

    @Test
    void fetchAllPaginated() {
    }

    @Test
    void fetchById() {
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