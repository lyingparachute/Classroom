package com.example.classroom.api;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.model.Department;
import com.example.classroom.model.Student;
import com.example.classroom.model.Subject;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeacherRestControllerIntegrationTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ModelMapper mapper;

    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetTeacher() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department = initData.createDepartmentOne(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        URI url = createURL("/api/teachers/" + expected.getId());
        ResponseEntity<TeacherDto> response = restTemplate.getForEntity(url, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertAll("Teacher properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Teacher", "ID").isEqualTo(expected.getId()),
                () -> assertThat(actual.getFirstName())
                        .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                () -> assertThat(actual.getLastName())
                        .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                () -> assertThat(actual.getEmail())
                        .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getAge())
                        .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge()),
                () -> assertThat(actual.getDepartment())
                        .as("Check %s's %s", "Teacher", "Department").isNotNull().isEqualTo(department),
                () -> assertThat(actual.getSubjects())
                        .as("Check if %s contains %s", "actualTeacher", "subjects")
                        .contains(subject1, subject2),
                () -> assertThat(actual.getStudents())
                        .as("Check if %s contains %s", "actualTeacher", "students")
                        .contains(student1, student2)
        );
    }

    @Test
    void shouldGetAllTeachers() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher expectedTeacher1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
        Teacher expectedTeacher2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
        //when
        URI url = createURL("/api/teachers");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set<TeacherDto> actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    void shouldCreateTeacher() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department = initData.createDepartmentOne(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        TeacherDto expected = createTeacherDto(department, List.of(subject1, subject2), List.of(student1, student2));
        Teacher expectedEntity = mapper.map(expected, Teacher.class);

        //when
        URI url = createURL("/api/teachers");
        ResponseEntity<TeacherDto> response = restTemplate.postForEntity(url, expected, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TeacherDto actual = response.getBody();
        assertThat(actual).as("Check if %s is not null", "Teacher").isNotNull();
        assertAll("Teacher's properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Teacher", "Id").isNotNull(),
                () -> assertThat(actual.getFirstName())
                        .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                () -> assertThat(actual.getLastName())
                        .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                () -> assertThat(actual.getEmail())
                        .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getAge())
                        .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge())
        );
        assertThat(actual.getDepartment()).as("Check if %s is not null", "Teacher's department").isNotNull();
        assertAll("Teacher's department properties",
                () -> assertThat(department.getId())
                        .as("Check %s's %s %s", "Teacher", "department", "Id").isEqualTo(department.getId()),
                () -> assertThat(department.getName())
                        .as("Check %s's %s %s", "Teacher", "department", "Name").isEqualTo(department.getName()),
                () -> assertThat(department.getAddress())
                        .as("Check %s's %s %s", "Teacher", "department", "Address").isEqualTo(department.getAddress()),
                () -> assertThat(department.getTelNumber())
                        .as("Check %s's %s %s", "Teacher", "department", "Telephone Number").isEqualTo(department.getTelNumber()),
                () -> assertThat(department.getDean())
                        .as("Check %s's %s", "Department", "dean (teacher)")
                        .isNotNull().isEqualTo(expectedEntity)
        );
        assertThat(actual.getSubjects()).as("Check %s's %s properties", "Teacher", "subjects")
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                subject1.getSemester(), subject1.getHoursInSemester()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                subject2.getSemester(), subject2.getHoursInSemester()));
        assertThat(actual.getStudents()).as("Check %s's %s properties", "Teacher", "students")
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
        assertThat(subject1.getTeachers())
                .as("Check if %s %s list contains %s", "subject1", "teachers", "teacher")
                .isNotNull().isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(subject2.getTeachers())
                .as("Check if %s %s list contains %s", "subject2", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(student1.getTeachers())
                .as("Check if %s %s list contains %s", "student1", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(expectedEntity);
        assertThat(student2.getTeachers())
                .as("Check if %s %s list contains %s", "student2", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(expectedEntity);
    }

    @Test
    void shouldUpdateTeacher() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department = initData.createDepartmentOne(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher teacherEntity = initData.createTeacherOne(null, List.of(), List.of());
        TeacherDto expected = createTeacherDto(department, List.of(subject1, subject2), List.of(student1, student2));
        expected.setId(teacherEntity.getId());
        //when
        URI url = createURL("/api/teachers/");
        final HttpEntity<TeacherDto> requestUpdate = new HttpEntity<>(expected);
        ResponseEntity<TeacherDto> response = restTemplate.exchange(url, HttpMethod.PUT, requestUpdate, TeacherDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TeacherDto actual = response.getBody();
        assertThat(actual).isNotNull();
        teacherRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Teacher with ID= " + actual.getId() + " should not be missing."));

        assertThat(actual).as("Check if %s is not null", "Teacher").isNotNull();
        assertAll("Teacher's properties",
                () -> assertThat(actual.getId())
                        .as("Check %s's %s", "Teacher", "Id").isNotNull(),
                () -> assertThat(actual.getFirstName())
                        .as("Check %s's %s", "Teacher", "First Name").isEqualTo(expected.getFirstName()),
                () -> assertThat(actual.getLastName())
                        .as("Check %s's %s", "Teacher", "Last Name").isEqualTo(expected.getLastName()),
                () -> assertThat(actual.getEmail())
                        .as("Check %s's %s", "Teacher", "Email").isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getAge())
                        .as("Check %s's %s", "Teacher", "Age").isEqualTo(expected.getAge())
        );
        assertThat(actual.getDepartment()).as("Check if %s is not null", "Teacher's department").isNotNull();
        assertAll("Teacher's department properties",
                () -> assertThat(department.getId())
                        .as("Check %s's %s %s", "Teacher", "department", "Id").isEqualTo(department.getId()),
                () -> assertThat(department.getName())
                        .as("Check %s's %s %s", "Teacher", "department", "Name").isEqualTo(department.getName()),
                () -> assertThat(department.getAddress())
                        .as("Check %s's %s %s", "Teacher", "department", "Address").isEqualTo(department.getAddress()),
                () -> assertThat(department.getTelNumber())
                        .as("Check %s's %s %s", "Teacher", "department", "Telephone Number").isEqualTo(department.getTelNumber()),
                () -> assertThat(department.getDean())
                        .as("Check %s's %s", "Department", "dean (teacher)")
                        .isNotNull().isEqualTo(teacherEntity)
        );
        assertThat(actual.getSubjects()).as("Check %s's %s properties", "Teacher", "subjects")
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(),
                                subject1.getSemester(), subject1.getHoursInSemester()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(),
                                subject2.getSemester(), subject2.getHoursInSemester()));
        assertThat(actual.getStudents()).as("Check %s's %s properties", "Teacher", "students")
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
        assertThat(subject1.getTeachers())
                .as("Check if %s %s list contains %s", "subject1", "teachers", "teacher")
                .isNotNull().isNotEmpty().hasSize(1).contains(teacherEntity);
        assertThat(subject2.getTeachers())
                .as("Check if %s %s list contains %s", "subject2", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(teacherEntity);
        assertThat(student1.getTeachers())
                .as("Check if %s %s list contains %s", "student1", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(teacherEntity);
        assertThat(student2.getTeachers())
                .as("Check if %s %s list contains %s", "student2", "teachers", "teacher")
                .isNotEmpty().hasSize(1).contains(teacherEntity);
    }

    @Test
    void shouldDeleteTeacher() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department = initData.createDepartmentOne(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        URI url = createURL("/api/teachers/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<Teacher> byId = teacherRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        departmentRepository.findById(department.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + department.getId() + " and name " +
                        department.getName() + " should not be removed."));
        expected.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID= " + student.getId() + " and name " +
                                student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
        expected.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " +
                                subject.getName() + " should not be removed.")));
    }

    @Test
    void shouldDeleteAllTeachers() throws URISyntaxException {
        //given
        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Teacher expected1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
        Teacher expected2 = initData.createTeacherTwo(department2, List.of(subject2), List.of(student2));
        //when
        URI url = createURL("/api/teachers");
        restTemplate.delete(url);
        //then
        Optional<Teacher> byId1 = teacherRepository.findById(expected1.getId());
        assertThat(byId1).isNotPresent();
        departmentRepository.findById(department1.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + department1.getId() + " and name " +
                        department1.getName() + " should not be removed."));
        expected1.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID= " + student.getId() + " and name " +
                                student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
        expected1.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " +
                                subject.getName() + " should not be removed.")));

        Optional<Teacher> byId2 = teacherRepository.findById(expected1.getId());
        assertThat(byId2).isNotPresent();
        departmentRepository.findById(department2.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + department2.getId() + " and name " +
                        department2.getName() + " should not be removed."));
        expected2.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID= " + student.getId() + " and name " +
                                student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
        expected2.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " +
                                subject.getName() + " should not be removed.")));
    }

    private TeacherDto createTeacherDto(Department department, List<Subject> subjects, List<Student> students) {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Lionel");
        teacherDto.setLastName("Messi");
        teacherDto.setEmail("l.messi@gmail.com");
        teacherDto.setAge(35);
        teacherDto.setStudents(new HashSet<>(students));
        teacherDto.setDepartment(department);
        teacherDto.setSubjects(new HashSet<>(subjects));
        return teacherDto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }

}