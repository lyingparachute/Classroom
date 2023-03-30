package com.example.classroom.api;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.model.Department;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Student;
import com.example.classroom.model.Subject;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import jakarta.transaction.Transactional;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FieldOfStudyRestControllerIntegrationTest {

    @Autowired
    private IntegrationTestsInitData initData;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private FieldOfStudyRepository fieldOfStudyRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
    }

    @Test
    void shouldGetAllFieldsOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(department2, List.of(subject2), List.of(student2));
        //when
        URI url = createURL("/api/fields-of-study");
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Set actual = response.getBody();
        assertThat(actual).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    @Transactional
    void shouldCreateFieldOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());

        FieldOfStudyDto expected = createFieldOfStudyDto(department1, List.of(subject1), List.of(student1));

        //when
        URI url = createURL("/api/fields-of-study");
        ResponseEntity<FieldOfStudyDto> response = restTemplate.postForEntity(url, expected, FieldOfStudyDto.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        FieldOfStudyDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        fieldOfStudyRepository.findById(actual.getId()).orElseThrow(() -> new IllegalStateException(
                "Field Of Study with ID = " + actual.getId() + " and name " + actual.getName() + " should not be missing."));
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(expected.getDepartment());
        assertThat(actual.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getName(), expected.getLevelOfEducation(), expected.getMode(), expected.getTitle()));
        assertThat(actual.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(), subject1.getSemester(),
                                subject1.getHoursInSemester(), expected, subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), expected, subject2.getTeachers()));
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getAge(),
                                student1.getEmail(), expected, student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), expected, student2.getTeachers()));
    }

    @Test
    void shouldGetFieldOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());
        Department department = initData.createDepartmentOne(null, List.of());

        FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        URI url = createURL("/api/fields-of-study/" + expected.getId());
        ResponseEntity<FieldOfStudyDto> response = restTemplate.getForEntity(url, FieldOfStudyDto.class);
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findById(expected.getId());
        assertThat(byId).isPresent();
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        FieldOfStudyDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(department);
        assertThat(actual.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(), subject1.getSemester(),
                                subject1.getHoursInSemester(), expected, subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), expected, subject2.getTeachers()));
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getAge(),
                                student1.getEmail(), expected, student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), expected, student2.getTeachers()));
    }

    @Test
    void shouldUpdateFieldOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department = initData.createDepartmentOne(null, List.of());
        FieldOfStudy entity = initData.createFieldOfStudyOne(null, List.of(), List.of());

        FieldOfStudyDto expected = createFieldOfStudyDto(department, List.of(subject1, subject2), List.of(student1, student2));
        expected.setId(entity.getId());

        //when
        URI url = createURL("/api/fields-of-study/");
        final HttpEntity<FieldOfStudyDto> request = new HttpEntity<>(expected);
        ResponseEntity<FieldOfStudyDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, FieldOfStudyDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        FieldOfStudyDto actual = response.getBody();
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        fieldOfStudyRepository.findById(actual.getId()).orElseThrow(
                () -> new IllegalStateException(
                        "Field Of Study with ID = " + actual.getId() + " and name " + actual.getName() + " should not be missing."));
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLevelOfEducation()).isEqualTo(expected.getLevelOfEducation());
        assertThat(actual.getMode()).isEqualTo(expected.getMode());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDepartment()).isEqualTo(expected.getDepartment());
        assertThat(actual.getDepartment().getFieldsOfStudy())
                .extracting(
                        FieldOfStudy::getId,
                        FieldOfStudy::getName,
                        FieldOfStudy::getLevelOfEducation,
                        FieldOfStudy::getMode,
                        FieldOfStudy::getTitle
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(expected.getId(), expected.getName(), expected.getLevelOfEducation(),
                                expected.getMode(), expected.getTitle()));
        assertThat(actual.getSubjects())
                .extracting(
                        Subject::getId,
                        Subject::getName,
                        Subject::getDescription,
                        Subject::getSemester,
                        Subject::getHoursInSemester,
                        Subject::getFieldOfStudy,
                        Subject::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(subject1.getId(), subject1.getName(), subject1.getDescription(), subject1.getSemester(),
                                subject1.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject1.getTeachers()),
                        Tuple.tuple(subject2.getId(), subject2.getName(), subject2.getDescription(), subject2.getSemester(),
                                subject2.getHoursInSemester(), fieldOfStudyRepository.findAll().get(0), subject2.getTeachers()));
        assertThat(actual.getStudents())
                .extracting(
                        Student::getId,
                        Student::getFirstName,
                        Student::getLastName,
                        Student::getAge,
                        Student::getEmail,
                        Student::getFieldOfStudy,
                        Student::getTeachers
                ).containsExactlyInAnyOrder(
                        Tuple.tuple(student1.getId(), student1.getFirstName(), student1.getLastName(), student1.getAge(),
                                student1.getEmail(), fieldOfStudyRepository.findAll().get(0), student1.getTeachers()),
                        Tuple.tuple(student2.getId(), student2.getFirstName(), student2.getLastName(), student2.getAge(),
                                student2.getEmail(), fieldOfStudyRepository.findAll().get(0), student2.getTeachers()));
    }

    @Test
    void shouldDeleteFieldOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department = initData.createDepartmentOne(null, List.of());

        FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        URI url = createURL("/api/fields-of-study/" + expected.getId());
        restTemplate.delete(url);
        //then
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findById(expected.getId());
        assertThat(byId).isNotPresent();
        departmentRepository.findById(department.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + department.getId() + " and name " + department.getName() + " should not be removed."));
        expected.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        expected.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));
    }

    @Test
    void shouldDeleteAllFieldsOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentThree(null, List.of());

        FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));
        FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyOne(department2, List.of(subject2), List.of(student2));
        //when
        URI url = createURL("/api/fields-of-study");
        restTemplate.delete(url);
        //then
        Optional<FieldOfStudy> byId1 = fieldOfStudyRepository.findById(fieldOfStudy1.getId());
        assertThat(byId1).isNotPresent();
        departmentRepository.findById(department2.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + fieldOfStudy1.getDepartment().getId() + " and name " + fieldOfStudy1.getDepartment().getName() + " should not be removed."));
        fieldOfStudy1.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        fieldOfStudy1.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));

        Optional<FieldOfStudy> byId2 = fieldOfStudyRepository.findById(fieldOfStudy2.getId());
        assertThat(byId2).isNotPresent();
        departmentRepository.findById(department2.getId()).orElseThrow(() -> new IllegalStateException(
                "Department with ID = " + fieldOfStudy2.getDepartment().getId() + " and name " +
                        fieldOfStudy2.getDepartment().getName() + " should not be removed."));
        fieldOfStudy2.getSubjects().forEach(subject ->
                subjectRepository.findById(subject.getId()).orElseThrow(() -> new IllegalStateException(
                        "Subject with ID = " + subject.getId() + " and name " + subject.getName() + " should not be removed.")));
        fieldOfStudy2.getStudents().forEach(student ->
                studentRepository.findById(student.getId()).orElseThrow(() -> new IllegalStateException(
                        "Student with ID = " + student.getId() + " and name " + student.getFirstName() + " " + student.getLastName() + " should not be removed.")));

    }

    private FieldOfStudyDto createFieldOfStudyDto(Department department, List<Subject> subjects, List<Student> students) {
        FieldOfStudyDto dto = new FieldOfStudyDto();
        dto.setName("Inżynieria materiałowa");
        dto.setLevelOfEducation(LevelOfEducation.FIRST);
        dto.setMode(ModeOfStudy.PT);
        dto.setTitle(AcademicTitle.ENG);
        dto.setDepartment(department);
        dto.setSubjects(new HashSet<>(subjects));
        dto.setStudents(new HashSet<>(students));
        return dto;
    }

    private URI createURL(String path) throws URISyntaxException {
        return new URI("http://localhost:" + randomServerPort + path);
    }
}