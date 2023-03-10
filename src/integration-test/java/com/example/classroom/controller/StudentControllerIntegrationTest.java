package com.example.classroom.controller;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration")
@SpringBootTest
class StudentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private IntegrationTestsInitData integrationTestsInitData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        integrationTestsInitData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldCreateStudent() throws Exception {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());

        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Marek");
        studentDto.setLastName("Mostowiak");
        studentDto.setEmail("m.mostowiak@gmail.com");
        studentDto.setAge(22);
        //when
        this.mockMvc.perform(post("/dashboard/students/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content("firstName=" + studentDto.getFirstName() +
                        "&lastName=" + studentDto.getLastName() +
                        "&email=" + studentDto.getEmail() +
                        "&age=" + studentDto.getAge() +
                        "&teachers=" + teacher1.getId() +
                        "&_teachersList=on" +
                        "&teachers=" + teacher2.getId() +
                        "&_teachersList=on" +
                        "&add="))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Optional<Student> byId = studentRepository.findAll().stream().findFirst();
        assertThat(byId).isPresent();
        Student actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(studentDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(studentDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(studentDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(studentDto.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(studentDto.getFieldOfStudy());
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
    void shouldDeleteStudent() throws Exception {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Student student = integrationTestsInitData.createStudentOne(null, List.of(teacher1, teacher2));

        //when
        this.mockMvc.perform(get("/dashboard/students/delete/" + student.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        //then
        Optional<Student> byId = studentRepository.findById(student.getId());
        assertThat(byId).isNotPresent();
        student.getTeachers().forEach(i -> {
            teacherRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Teacher with ID = " + i.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldEditStudent() throws Exception {
        //given
        Teacher teacher1 = integrationTestsInitData.createTeacherOne(null, List.of(), List.of());
        Teacher teacher2 = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        Student studentEntity = integrationTestsInitData.createStudentOne(null, List.of());

        StudentDto studentDto = new StudentDto();
        studentDto.setFirstName("Marek");
        studentDto.setLastName("Mostowiak");
        studentDto.setEmail("m.mostowiak@gmail.com");
        studentDto.setAge(22);
        //when
        this.mockMvc.perform(post("/dashboard/students/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("id=" + studentEntity.getId() +
                                "&firstName=" + studentDto.getFirstName() +
                                "&lastName=" + studentDto.getLastName() +
                                "&email=" + studentDto.getEmail() +
                                "&age=" + studentDto.getAge() +
                                "&teachers=" + teacher1.getId() +
                                "&_teachersList=on" +
                                "&teachers=" + teacher2.getId() +
                                "&_teachersList=on" +
                                "&add="))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Optional<Student> byId = studentRepository.findById(studentEntity.getId());
        assertThat(byId).isPresent();
        Student actual = byId.get();

        assertThat(actual.getFirstName()).isEqualTo(studentDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(studentDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(studentDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(studentDto.getAge());
        assertThat(actual.getFieldOfStudy()).isEqualTo(studentDto.getFieldOfStudy());
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
}