package systems.ultimate.classroom.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.StudentRepository;
import systems.ultimate.classroom.repository.util.InitData;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StudentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void getPaginatedStudents() {
    }

    @Test
    void getStudent() {
    }

    @Test
    void shouldCreateStudent() throws Exception {
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());

        this.mockMvc.perform(post("/students/new")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content(""))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteStudent() {
    }

    @Test
    void editStudent() {
    }
}