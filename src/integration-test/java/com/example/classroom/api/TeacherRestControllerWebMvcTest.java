package com.example.classroom.api;

import com.example.classroom.dto.TeacherDto;
import com.example.classroom.model.*;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherRestController.class)
class TeacherRestControllerWebMvcTest {

    @MockBean
    private TeacherService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ModelMapper mapper;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class GetTeacher {
        @Test
        void returns200_withDtoInBody_givenExistingId() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            given(service.fetchById(expected.getId())).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/teachers/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            TeacherDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TeacherDto.class);
            then(service).should().fetchById(anyLong());
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
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "Teacher", "Department").isNotNull().isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns404_givenNonExistingId() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchById(id)).willThrow(new IllegalArgumentException(
                    "Invalid Teacher ID: " + id));
            //when
            mockMvc.perform(get("/api/teachers/{id}", id))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchById(id);
        }

        @Test
        void returns404_givenNull() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            given(service.fetchById(expected.getId())).willReturn(null);
            //when
            mockMvc.perform(get("/api/teachers/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andDo(print());
            //then
            then(service).should().fetchById(expected.getId());
        }
    }

    @Nested
    class GetTeachers {
        @Test
        void returns200_withListOfTeachersInBody() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentTwo(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());

            Teacher expected1 = initData.createTeacherOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            Teacher expected2 = initData.createTeacherOne(department2, List.of(subject3), List.of(student3));
            TeacherDto dto1 = mapper.map(expected1, TeacherDto.class);
            TeacherDto dto2 = mapper.map(expected2, TeacherDto.class);
            given(service.fetchAll()).willReturn(List.of(dto1, dto2));
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/teachers"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            List<LinkedHashMap> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), List.class);
            then(service).should().fetchAll();
            assertThat(actual).isNotNull().hasSize(2);
            var actualTeacher1 = actual.get(0);
            var actualDepartment1 = (LinkedHashMap) actualTeacher1.get("department");
            var actualTeacher2 = actual.get(1);
            var actualDepartment2 = (LinkedHashMap) actualTeacher2.get("department");
            assertThat(actualTeacher1).as("Check teacher1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected1.getId().intValue()),
                            entry("firstName", expected1.getFirstName()),
                            entry("lastName", expected1.getLastName()),
                            entry("email", expected1.getEmail()),
                            entry("age", expected1.getAge())
                    ));
            assertThat(actualDepartment1).as("Check teacher1's department properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", department1.getId().intValue()),
                            entry("name", department1.getName()),
                            entry("address", department1.getAddress()),
                            entry("telNumber", department1.getTelNumber())
                    ));
            assertThat(actualTeacher2).as("Check teacher2 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected2.getId().intValue()),
                            entry("firstName", expected2.getFirstName()),
                            entry("lastName", expected2.getLastName()),
                            entry("email", expected2.getEmail()),
                            entry("age", expected2.getAge())
                    ));
            assertThat(actualDepartment2).as("Check teacher2's department properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", department2.getId().intValue()),
                            entry("name", department2.getName()),
                            entry("address", department2.getAddress()),
                            entry("telNumber", department2.getTelNumber())
                    ));
        }

        @Test
        void returns404_whenNoTeachersInDatabase() throws Exception {
            //given
            given(service.fetchAll()).willReturn(List.of());
            //when
            mockMvc.perform(get("/api/teachers"))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchAll();
        }
    }

    @Nested
    class CreateTeacher {
        @Test
        void returns201_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            given(service.create(any(TeacherDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(any(TeacherDto.class));
            TeacherDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TeacherDto.class);
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
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "Teacher", "Department").isNotNull().isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            given(service.create(any(TeacherDto.class))).willReturn(null);
            //when
            mockMvc.perform(post("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(any(TeacherDto.class));
        }

        @Test
        void returns400_withErrorMsg_givenEmptyDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForFirstNameEmpty = "First name cannot be empty.";
            String expectedErrorMsgForLastNameEmpty = "Last name cannot be empty.";
            String expectedErrorMsgForAge = "Teacher must be at lest 21 years old.";
            String expectedErrorMsgForEmail = "Email cannot be empty.";
            Teacher expected = new Teacher();
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForFirstNameEmpty,
                            expectedErrorMsgForLastNameEmpty,
                            expectedErrorMsgForAge,
                            expectedErrorMsgForEmail);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForFirstNameLength = "First name must be between 2 and 30 characters long.";
            String expectedErrorMsgForLastNameLength = "Last name must be between 2 and 30 characters long.";
            String expectedErrorMsgForAge = "This Teacher is too old. Max age is 80.";
            String expectedErrorMsgForEmail = "Enter valid email address.";
            Teacher expected = new Teacher();
            expected.setFirstName("too long first name ssssssssssssssssssssssss");
            expected.setLastName("too long last name ssssssssssssssss");
            expected.setAge(81);
            expected.setEmail("student");
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForFirstNameLength,
                            expectedErrorMsgForLastNameLength,
                            expectedErrorMsgForAge,
                            expectedErrorMsgForEmail);
        }
    }

    @Nested
    class UpdateTeacher {
        @Test
        void returns200_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            given(service.update(any(TeacherDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().update(any(TeacherDto.class));
            TeacherDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), TeacherDto.class);
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
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "Teacher", "Department").isNotNull().isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "actualTeacher", "subjects")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "actualTeacher", "students")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Teacher expected = initData.createTeacherOne(department, List.of(subject1, subject2), List.of(student1, student2));
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            given(service.update(any(TeacherDto.class))).willReturn(null);
            //when
            mockMvc.perform(put("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().update(dto);
        }

        @Test
        void returns400_withErrorMsg_givenEmptyDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForFirstNameEmpty = "First name cannot be empty.";
            String expectedErrorMsgForLastNameEmpty = "Last name cannot be empty.";
            String expectedErrorMsgForAge = "Teacher must be at lest 21 years old.";
            String expectedErrorMsgForEmail = "Email cannot be empty.";
            Teacher expected = new Teacher();
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForFirstNameEmpty,
                            expectedErrorMsgForLastNameEmpty,
                            expectedErrorMsgForAge,
                            expectedErrorMsgForEmail);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForFirstNameLength = "First name must be between 2 and 30 characters long.";
            String expectedErrorMsgForLastNameLength = "Last name must be between 2 and 30 characters long.";
            String expectedErrorMsgForAge = "This Teacher is too old. Max age is 80.";
            String expectedErrorMsgForEmail = "Enter valid email address.";
            Teacher expected = new Teacher();
            expected.setFirstName("too long first name ssssssssssssssssssssssss");
            expected.setLastName("too long last name ssssssssssssssss");
            expected.setAge(81);
            expected.setEmail("student");
            TeacherDto dto = mapper.map(expected, TeacherDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/teachers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForFirstNameLength,
                            expectedErrorMsgForLastNameLength,
                            expectedErrorMsgForAge,
                            expectedErrorMsgForEmail);
        }
    }

    @Nested
    class DeleteTeacher {
        @Test
        void returns202_givenCorrectId() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            mockMvc.perform(delete("/api/teachers/{id}", expected.getId()))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().remove(expected.getId());
        }
    }

    @Nested
    class DeleteAllTeachers {
        @Test
        void returns202() throws Exception {
            mockMvc.perform(delete("/api/teachers"))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().removeAll();
        }
    }
}