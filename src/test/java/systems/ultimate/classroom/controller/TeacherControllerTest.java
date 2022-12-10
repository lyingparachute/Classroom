package systems.ultimate.classroom.controller;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import systems.ultimate.classroom.dto.TeacherDto;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.enums.Subject;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.TeacherRepository;
import systems.ultimate.classroom.repository.util.InitData;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class TeacherControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private InitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldCreateTeacher() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setFirstName("Marek");
        teacherDto.setLastName("Mostowiak");
        teacherDto.setEmail("m.mostowiak@gmail.com");
        teacherDto.setAge(42);
        teacherDto.setSubject(Subject.MATHS);
        //when
        this.mockMvc.perform(post("/teachers/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("firstName=" + teacherDto.getFirstName() +
                                "&lastName=" + teacherDto.getLastName() +
                                "&email=" + teacherDto.getEmail() +
                                "&age=" + teacherDto.getAge() +
                                "&subject=" + teacherDto.getSubject() +
                                "&studentsList=" + student1.getId() +
                                "&_studentsList=on" +
                                "&studentsList=" + student2.getId() +
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
        assertThat(actual.getSubject()).isEqualTo(teacherDto.getSubject());
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
    void shouldDeleteStudent() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Teacher teacher = initData.createTeacherTwo(List.of(student1, student2));
        //when
        this.mockMvc.perform(get("/teachers/delete/" + teacher.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        //then
        Optional<Teacher> byId = teacherRepository.findById(teacher.getId());
        assertThat(byId).isNotPresent();
        teacher.getStudentsList().forEach(i -> {
            studentRepository.findById(i.getId()).orElseThrow(() -> new IllegalStateException(
                    "Student with ID= " + i.getId() + " should not be removed."));
        });
    }

    @Test
    void shouldEditTeacher() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Teacher teacherEntity = initData.createTeacherTwo(List.of());
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(teacherEntity.getId());
        teacherDto.setFirstName("Marek");
        teacherDto.setLastName("Mostowiak");
        teacherDto.setEmail("m.mostowiak@gmail.com");
        teacherDto.setAge(42);
        teacherDto.setSubject(Subject.MATHS);
        //when
        this.mockMvc.perform(post("/teachers/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("id=" + teacherEntity.getId() +
                                "&firstName=" + teacherDto.getFirstName() +
                                "&lastName=" + teacherDto.getLastName() +
                                "&email=" + teacherDto.getEmail() +
                                "&age=" + teacherDto.getAge() +
                                "&subject=" + teacherDto.getSubject() +
                                "&studentsList=" + student1.getId() +
                                "&_studentsList=on" +
                                "&studentsList=" + student2.getId() +
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
        assertThat(actual.getSubject()).isEqualTo(teacherDto.getSubject());
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
}