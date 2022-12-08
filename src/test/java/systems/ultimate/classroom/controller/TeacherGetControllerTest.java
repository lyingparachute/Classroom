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
class TeacherGetControllerTest {

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
    void shouldGetTeacherView() throws Exception {
        Teacher teacher1 = initData.createTeacherOne(List.of());
        this.mockMvc.perform(get("/teachers/" + teacher1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teacher"));
    }

    @Test
    void shouldGetParticularTeacher() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Teacher teacher = initData.createTeacherOne(List.of(student1, student2));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/teachers/" + teacher.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("<h1 class=\"card-header text-center m-4\">Viewing teacher with ID: " +  teacher.getId() +"</h1>");
        assertThat(contentAsString)
                .contains("                            <li class=\"list-group-item\">First Name: " + teacher.getFirstName() + "</li>\n" +
                        "                            <li class=\"list-group-item\">Last Name: " + teacher.getLastName() + "</li>\n" +
                        "                            <li class=\"list-group-item\">Email: " + teacher.getEmail() + "</li>\n" +
                        "                            <li class=\"list-group-item\">Age: " + teacher.getAge() + "</li>\n" +
                        "                            <li class=\"list-group-item\">Classes: " + teacher.getSubject().getDesc() + "</li>\n" +
                        "                            <li class=\"list-group-item\">List of assigned students:\n" +
                        "                                <ul class=\"list-group\">"
                );
        assertThat(contentAsString)
                .contains("                                       href=\"/students/" + student1.getId() + "\"\n" +
                        "                                       value=\"" + student1.getId() + "\">" + student1.getFirstName() + " " + student1.getLastName());
        assertThat(contentAsString)
                .contains("                                       href=\"/students/" + student2.getId() + "\"\n" +
                        "                                       value=\"" + student2.getId() + "\">" + student2.getFirstName() + " " + student2.getLastName());

    }

    @Test
    void shouldGetTeachersView() throws Exception {
        this.mockMvc.perform(get("/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teachers"));
    }

    @Test
    void shouldGetTeachersAndContainParticularTeacher() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher1 = initData.createTeacherOne(List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(List.of(student3));

        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/teachers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                             <td>" + teacher1.getFirstName()+ "</td>\n" +
                        "                                <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                <td>" + teacher1.getAge() + "</td>\n" +
                        "                                <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                <td>" + teacher1.getSubject().getDesc() + "</td>\n" +
                        "                                <td>" + teacher1.getStudentsList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                             <td>" + teacher2.getFirstName()+ "</td>\n" +
                        "                                <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                <td>" + teacher2.getAge() + "</td>\n" +
                        "                                <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                <td>" + teacher2.getSubject().getDesc() + "</td>\n" +
                        "                                <td>" + teacher2.getStudentsList().size() + "</td>\n");
    }

    @Test
    void shouldGetTeachersSecondPageSortedByFirstName() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher1 = initData.createTeacherOne(List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(List.of(student1, student2, student3));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/teachers/page/2?sortField=firstName&sortDir=asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // used sorting dir by lastName ascending, so student1 will be last - Jarosław
        assertThat(contentAsString)
                .contains("                             <td>" + teacher1.getFirstName()+ "</td>\n" +
                        "                                <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                <td>" + teacher1.getAge() + "</td>\n" +
                        "                                <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                <td>" + teacher1.getSubject().getDesc() + "</td>\n" +
                        "                                <td>" + teacher1.getStudentsList().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + teacher2.getFirstName()+ "</td>\n" +
                        "                                <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                <td>" + teacher2.getAge() + "</td>\n" +
                        "                                <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                <td>" + teacher2.getSubject().getDesc() + "</td>\n" +
                        "                                <td>" + teacher2.getStudentsList().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                             <td>" + student3.getFirstName()+ "</td>\n" +
                        "                                <td>" + teacher3.getLastName() + "</td>\n" +
                        "                                <td>" + teacher3.getAge() + "</td>\n" +
                        "                                <td>" + teacher3.getEmail() + "</td>\n" +
                        "                                <td>" + teacher3.getSubject().getDesc() + "</td>\n" +
                        "                                <td>" + teacher3.getStudentsList().size() + "</td>\n");
    }

    @Test
    void shouldGetTeachersSearchView() throws Exception {
        this.mockMvc.perform(get("/teachers/search?name=w"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(view().name("teachers-search"));
    }

    @Test
    void shouldGetResultOfSearchTeachersByFirstOrLastName() throws Exception {
        //given
        Student student1 = initData.createStudentOne(List.of());
        Student student2 = initData.createStudentTwo(List.of());
        Student student3 = initData.createStudentThree(List.of());
        Teacher teacher1 = initData.createTeacherOne(List.of(student1, student2));
        Teacher teacher2 = initData.createTeacherTwo(List.of(student3));
        Teacher teacher3 = initData.createTeacherThree(List.of(student1, student2, student3));
        //when
        MvcResult mvcResult = this.mockMvc.perform(get("/teachers/search?name=ja"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString)
                .contains("                                    <td>" + teacher1.getFirstName()+ "</td>\n" +
                        "                                    <td>" + teacher1.getLastName() + "</td>\n" +
                        "                                    <td>" + teacher1.getAge() + "</td>\n" +
                        "                                    <td>" + teacher1.getEmail() + "</td>\n" +
                        "                                    <td>" + teacher1.getSubject().getDesc() + "</td>\n" +
                        "                                    <td>" + teacher1.getStudentsList().size() + "</td>\n");
        assertThat(contentAsString)
                .contains("                                    <td>" + teacher2.getFirstName()+ "</td>\n" +
                        "                                    <td>" + teacher2.getLastName() + "</td>\n" +
                        "                                    <td>" + teacher2.getAge() + "</td>\n" +
                        "                                    <td>" + teacher2.getEmail() + "</td>\n" +
                        "                                    <td>" + teacher2.getSubject().getDesc() + "</td>\n" +
                        "                                    <td>" + teacher2.getStudentsList().size() + "</td>\n");
        assertThat(contentAsString)
                .doesNotContain("                                    <td>" + student3.getFirstName()+ "</td>\n" +
                        "                                    <td>" + teacher3.getLastName() + "</td>\n" +
                        "                                    <td>" + teacher3.getAge() + "</td>\n" +
                        "                                    <td>" + teacher3.getEmail() + "</td>\n" +
                        "                                    <td>" + teacher3.getSubject().getDesc() + "</td>\n" +
                        "                                    <td>" + teacher3.getStudentsList().size() + "</td>\n");
    }

}