package com.example.classroom.controller;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.StudentRepository;
import com.example.classroom.repository.TeacherRepository;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import jakarta.transaction.Transactional;
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
@Transactional
class TeacherControllerIntegrationTest {

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
    void shouldCreateTeacher() throws Exception {
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Marek");
        teacherDto.setLastName("Mostowiak");
        teacherDto.setEmail("m.mostowiak@gmail.com");
        teacherDto.setAge(42);
        //when
        this.mockMvc.perform(post("/dashboard/teachers/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("firstName=" + teacherDto.getFirstName() +
                                "&lastName=" + teacherDto.getLastName() +
                                "&email=" + teacherDto.getEmail() +
                                "&age=" + teacherDto.getAge() +
                                "&students=" + student1.getId() +
                                "&_studentsList=on" +
                                "&students=" + student2.getId() +
                                "&_studentsList=on" +
                                "&add="))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Optional<Teacher> first = teacherRepository.findAll().stream().findFirst();
        assertThat(first).isPresent();
        Teacher actual = first.get();
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
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
                                student2.getEmail(), student2.getAge()));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Teacher teacher = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of(student1, student2));
        //when
        this.mockMvc.perform(get("/dashboard/teachers/delete/" + teacher.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
        teacher.getStudents().forEach(i -> {
            studentRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Student with ID= " + i.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldEditTeacher() throws Exception {
        //given
        Student student1 = integrationTestsInitData.createStudentOne(null, List.of());
        Student student2 = integrationTestsInitData.createStudentTwo(null, List.of());
        Teacher teacherEntity = integrationTestsInitData.createTeacherTwo(null, List.of(), List.of());
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacherEntity.getId());
        teacherDto.setFirstName("Marek");
        teacherDto.setLastName("Mostowiak");
        teacherDto.setEmail("m.mostowiak@gmail.com");
        teacherDto.setAge(42);
        //when
        this.mockMvc.perform(post("/dashboard/teachers/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("id=" + teacherEntity.getId() +
                                "&firstName=" + teacherDto.getFirstName() +
                                "&lastName=" + teacherDto.getLastName() +
                                "&email=" + teacherDto.getEmail() +
                                "&age=" + teacherDto.getAge() +
                                "&students=" + student1.getId() +
                                "&_studentsList=on" +
                                "&students=" + student2.getId() +
                                "&_studentsList=on" +
                                "&add="))
                .andDo(print())
                .andExpect(status().isOk());
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacherEntity.getId());
        assertThat(byId).isPresent();
        Teacher actual = byId.get();
        assertThat(actual.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(actual.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(actual.getAge()).isEqualTo(teacherDto.getAge());
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
                                student2.getEmail(), student2.getAge()));
    }
}