package systems.ultimate.classroom.rest;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeacherRestControllerTest {
    @Autowired
    private InitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetTeacher() throws URISyntaxException {
        //given
        Student studentOne = initData.createStudentOne();
        Student studentTwo = initData.createStudentTwo();
        Teacher teacher = initData.createTeacherOne(List.of(studentOne, studentTwo));

        //when
        URI url = createURL("/api/teachers/" + teacher.getId());
        ResponseEntity<TeacherDto> response = restTemplate.getForEntity(url, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(teacher.getId());
        assertThat(actual.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacher.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacher.getSubject());
//        assertThat(actual.getStudentsList()).size().isEqualTo(1);
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(studentOne.getId(), studentOne.getFirstName(), studentOne.getLastName(),
                                studentOne.getEmail(), studentOne.getAge(), studentOne.getFieldOfStudy()),
                        Tuple.tuple(studentTwo.getId(), studentTwo.getFirstName(), studentTwo.getLastName(),
                                studentTwo.getEmail(), studentTwo.getAge(), studentTwo.getFieldOfStudy())
                );
    }

//    @Test
//    void shouldGetAllTeachers() throws URISyntaxException {
//        //given
//        initData.createTeacherOne( List.of(initData.createStudentOne(), initData.createStudentTwo()));
//        initData.createTeacherTwo();
//        //when
//        URI url = createURL("/api/teachers/");
//        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        Set actual = response.getBody();
//        assertThat(actual).isNotNull();
//        assertThat(actual).isNotEmpty();
//        assertThat(actual).size().isEqualTo(2);
//    }

    @Test
    void shouldCreateTeacher() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne();
        Student student2 = initData.createStudentTwo();

        TeacherDto teacherDto = createTeacherDto(student1, student2);
        //when
        URI url = createURL("/api/teachers/create");
        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(url, teacherDto ,TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Teacher with ID= " + actual.getId() + " should not be missing"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacherDto.getSubject());
        assertThat(actual.getStudentsList()).size().isEqualTo(2);
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getEmail(), student1.getAge(), student1.getFieldOfStudy()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getEmail(), student2.getAge(), student2.getFieldOfStudy())
                );
    }

    @Test
    void shouldUpdateTeacher() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne();
        Student student2 = initData.createStudentTwo();

        Teacher teacherEntity = initData.createTeacherOne(new ArrayList<>());
        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(teacherEntity.getId());
        teacherDto2.setFirstName("Lionel");
        teacherDto2.setLastName("Messi");
        teacherDto2.setEmail("l.messi@gmail.com");
        teacherDto2.setAge(35);
        teacherDto2.setSubject(Subject.ART);
        teacherDto2.setStudentsList(new HashSet<>(List.of(student1, student2)));

        //when
        URI url = createURL("/api/teachers/");
        final HttpEntity<TeacherDto> requestUpdate = new HttpEntity<>(teacherDto2);
        ResponseEntity<TeacherDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestUpdate ,TeacherDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Teacher with ID= " + actual.getId() + " should not be removed"));
        assertThat(actual.getFirstName()).isEqualTo(teacherDto2.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto2.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto2.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto2.getAge());
        assertThat(actual.getSubject()).isEqualTo(teacherDto2.getSubject());
        assertThat(actual.getStudentsList())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getEmail,
                        Student::getAge,
                        Student::getFieldOfStudy,
                        s -> s.getTeachersList().size()
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(),
                                student1.getEmail(), student1.getAge(), student1.getFieldOfStudy(), student1.getTeachersList().size()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(),
                                student2.getEmail(), student2.getAge(), student2.getFieldOfStudy(), student2.getTeachersList().size())
                );
    }

    @Test
    void shouldDeleteTeacher() throws URISyntaxException {
        //given
        Teacher teacher = initData.createTeacherOne(List.of(initData.createStudentOne(), initData.createStudentTwo()));
        //when
        URI url = createURL("/api/teachers/" + teacher.getId());
        restTemplate.delete(url);
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
        teacher.getStudentsList().forEach(i -> {
            assertThat(studentRepository.findById(i.getId()).isPresent()).isTrue();
            studentRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Student with ID= " + i.getId() + " should not be removed"));
        });
        assertThat(studentRepository.findAll().size()).isEqualTo(2);
    }

    private TeacherDto createTeacherDto(Student studentOne, Student studentTwo){
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Jagoda");
        teacherDto.setLastName("Kowalska");
        teacherDto.setEmail("j.kowalska@gmail.com");
        teacherDto.setAge(33);
        teacherDto.setSubject(Subject.SCIENCE);
        HashSet<Student> students = new HashSet<>();
        students.add(studentOne);
        students.add(studentTwo);
        teacherDto.setStudentsList(students);
        return teacherDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}