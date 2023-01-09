package com.example.classroom.rest;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.*;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.ModeOfStudy;
import com.example.classroom.repository.DepartmentRepository;
import com.example.classroom.repository.FieldOfStudyRepository;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.SubjectRepository;
import com.example.classroom.repository.util.InitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test-it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class FieldOfStudyRestControllerTest {

    @Autowired
    private InitData initData;

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
        assertThat(actual).isNotNull();
        assertThat(actual).size().isEqualTo(2);
    }

    @Test
    void shouldCreateFieldOfStudy() throws URISyntaxException {
        //given
        Student student1 = initData.createStudentOne(null, List.of());
        Student student2 = initData.createStudentTwo(null, List.of());

        Subject subject1 = initData.createSubjectOne(null, List.of());
        Subject subject2 = initData.createSubjectTwo(null, List.of());

        Department department1 = initData.createDepartmentOne(null, List.of());
        Department department2 = initData.createDepartmentTwo(null, List.of());

        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(department1, List.of(subject1), List.of(student1));

        //when
        URI url = createURL("/api/fields-of-study/" + fieldOfStudy.getId());
        ResponseEntity<Set> response = restTemplate.getForEntity(url, Set.class);

    }

    @Test
    void shouldGetFieldOfStudy() throws URISyntaxException {
        //given
        Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());

        Student student1 = initData.createStudentOne(null, List.of(teacher1));
        Student student2 = initData.createStudentTwo(null, List.of(teacher2));

        Subject subject1 = initData.createSubjectOne(null, List.of(teacher1));
        Subject subject2 = initData.createSubjectTwo(null, List.of(teacher2));

        Department department = initData.createDepartmentOne(teacher1, List.of());

        FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
        //when
        URI url = createURL("/api/fields-of-study/" + fieldOfStudy.getId());
        ResponseEntity<FieldOfStudyDto> response = restTemplate.getForEntity(url, FieldOfStudyDto.class);
        //then
        List<FieldOfStudy> all = fieldOfStudyRepository.findAll();
        Optional<FieldOfStudy> byId = fieldOfStudyRepository.findById(fieldOfStudy.getId());
        assertThat(byId).isPresent();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        FieldOfStudyDto actual = response.getBody();
        assertThat(actual).isNotNull();
    }

    @Test
    void shouldUpdateFieldOfStudy() {

    }

    @Test
    void shouldDeleteFieldOfStudy() {

    }

    @Test
    void shouldDeleteAllFieldsOfStudy() {

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