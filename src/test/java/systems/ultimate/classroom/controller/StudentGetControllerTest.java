package systems.ultimate.classroom.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import systems.ultimate.classroom.entity.Student;
import systems.ultimate.classroom.entity.Teacher;
import systems.ultimate.classroom.repository.util.InitData;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class StudentGetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private InitData initData;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        initData.cleanUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldGetStudentView() throws Exception {
        Student student = initData.createStudentOne(List.of());

        this.mockMvc.perform(get("/students/" + student.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("student"));
    }

    @Test
    void shouldGetParticularStudent() throws Exception {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student = initData.createStudentOne(List.of(teacher1, teacher2));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/students/" + student.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("<h1 class=\"card-header text-center m-4\">Viewing Student with ID: " +  student.getId() +"</h1>");
        assertThat(contentAsString)
                .contains("                        <li class=\"list-group-item\">First Name: " + student.getFirstName() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Last Name: " + student.getLastName() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Email: " + student.getEmail() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Age: " + student.getAge() + "</li>\n" +
                        "                        <li class=\"list-group-item\">Field of study: " + student.getFieldOfStudy().getDesc() + "</li>\n" +
                        "                        <li class=\"list-group-item\">List of assigned teachers:\n" +
                        "                            <ul class=\"list-group\">\n"
                );
        assertThat(contentAsString)
                .contains("                                   href=\"/teachers/" + teacher1.getId() + "\"\n" +
                        "                                   value=\"" + teacher1.getId() + "\">" + teacher1.getFirstName() + " " + teacher1.getLastName());
        assertThat(contentAsString)
                .contains("                                   href=\"/teachers/" + teacher2.getId() + "\"\n" +
                        "                                   value=\"" + teacher2.getId() + "\">" + teacher2.getFirstName() + " " + teacher2.getLastName());
    }

    @Test
    void shouldGetStudentsView() throws Exception {
        this.mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("students"));
    }

    @Test
    void shouldGetStudentsAndContainParticularStudents() throws Exception {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student1 = initData.createStudentOne(List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(List.of(teacher1));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                             <td>" + student1.getFirstName()+ "</td>\n" +
                        "                                <td>" + student1.getLastName() + "</td>\n" +
                        "                                <td>" + student1.getAge() + "</td>\n" +
                        "                                <td>" + student1.getEmail() + "</td>\n" +
                        "                                <td>" + student1.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                <td>" + student1.getTeachersList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                             <td>" + student2.getFirstName()+ "</td>\n" +
                        "                                <td>" + student2.getLastName() + "</td>\n" +
                        "                                <td>" + student2.getAge() + "</td>\n" +
                        "                                <td>" + student2.getEmail() + "</td>\n" +
                        "                                <td>" + student2.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                <td>" + student2.getTeachersList().size() + "</td>\n");
    }

    @Test
    void shouldGetStudentsSecondPageSortedByFirstName() throws Exception {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student1 = initData.createStudentOne(List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(List.of(teacher1));
        Student student3 = initData.createStudentThree(List.of(teacher1, teacher2));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/students/page/2?sortField=firstName&sortDir=asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // used sorting dir by lastName ascending, so student2 will be last - Weronika
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + student1.getFirstName()+ "</td>\n" +
                        "                                <td>" + student1.getLastName() + "</td>\n" +
                        "                                <td>" + student1.getAge() + "</td>\n" +
                        "                                <td>" + student1.getEmail() + "</td>\n" +
                        "                                <td>" + student1.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                <td>" + student1.getTeachersList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                             <td>" + student2.getFirstName()+ "</td>\n" +
                        "                                <td>" + student2.getLastName() + "</td>\n" +
                        "                                <td>" + student2.getAge() + "</td>\n" +
                        "                                <td>" + student2.getEmail() + "</td>\n" +
                        "                                <td>" + student2.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                <td>" + student2.getTeachersList().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + student3.getFirstName()+ "</td>\n" +
                        "                                <td>" + student3.getLastName() + "</td>\n" +
                        "                                <td>" + student3.getAge() + "</td>\n" +
                        "                                <td>" + student3.getEmail() + "</td>\n" +
                        "                                <td>" + student3.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                <td>" + student3.getTeachersList().size() + "</td>\n");

    }

    @Test
    void shouldGetStudentsSearchView() throws Exception {
        this.mockMvc.perform(get("/students/search?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("students-search"));
    }

    @Test
    void shouldGetResultOfSearchStudentsByFirstOrLastName() throws Exception {
        //given
        Teacher teacher1 = initData.createTeacherOne(List.of());
        Teacher teacher2 = initData.createTeacherTwo(List.of());
        Student student1 = initData.createStudentOne(List.of(teacher1, teacher2));
        Student student2 = initData.createStudentTwo(List.of(teacher1));
        Student student3 = initData.createStudentThree(List.of(teacher1, teacher2));

        //when
        // Searching for letter 'w' in first name or last name
        MvcResult mvcResult = this.mockMvc.perform(get("/students/search?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .doesNotContain("                                 <td>" + student1.getFirstName()+ "</td>\n" +
                        "                                    <td>" + student1.getLastName() + "</td>\n" +
                        "                                    <td>" + student1.getAge() + "</td>\n" +
                        "                                    <td>" + student1.getEmail() + "</td>\n" +
                        "                                    <td>" + student1.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                    <td>" + student1.getTeachersList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                                   <td>" + student2.getFirstName()+ "</td>\n" +
                        "                                    <td>" + student2.getLastName() + "</td>\n" +
                        "                                    <td>" + student2.getAge() + "</td>\n" +
                        "                                    <td>" + student2.getEmail() + "</td>\n" +
                        "                                    <td>" + student2.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                    <td>" + student2.getTeachersList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                                   <td>" + student3.getFirstName()+ "</td>\n" +
                        "                                    <td>" + student3.getLastName() + "</td>\n" +
                        "                                    <td>" + student3.getAge() + "</td>\n" +
                        "                                    <td>" + student3.getEmail() + "</td>\n" +
                        "                                    <td>" + student3.getFieldOfStudy().getDesc() + "</td>\n" +
                        "                                    <td>" + student3.getTeachersList().size() + "</td>\n");

    }
}