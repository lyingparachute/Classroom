package com.example.classroom.api;

import com.example.classroom.dto.StudentDto;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Student;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.service.StudentService;
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

@WebMvcTest(StudentRestController.class)
class StudentRestControllerWebMvcTest {

    @MockBean
    private StudentService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ModelMapper mapper;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class GetStudent {
        @Test
        void returns200_withDtoInBody_givenExistingId() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            given(service.fetchById(expected.getId())).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/students/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            StudentDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), StudentDto.class);
            then(service).should().fetchById(anyLong());
            assertAll("Student properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s %s", "student", "first name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s %s", "student", "last name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s %s", "student", "ager").isEqualTo(expected.getAge()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s %s", "student", "email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "student", "field of study").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "student", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns404_givenNonExistingId() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchById(id)).willThrow(new IllegalArgumentException(
                    "Invalid Student ID: " + id));
            //when
            mockMvc.perform(get("/api/students/{id}", id))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchById(id);
        }

        @Test
        void returns404_givenNull() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            given(service.fetchById(expected.getId())).willReturn(null);
            //when
            mockMvc.perform(get("/api/students/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andDo(print());
            //then
            then(service).should().fetchById(expected.getId());
        }
    }

    @Nested
    class GetStudents {
        @Test
        void returns200_withListOfStudentsInBody() throws Exception {
            //given
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            Student expected1 = initData.createStudentOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Student expected2 = initData.createStudentTwo(fieldOfStudy2, List.of(teacher3));
            StudentDto dto1 = mapper.map(expected1, StudentDto.class);
            StudentDto dto2 = mapper.map(expected2, StudentDto.class);
            given(service.fetchAll()).willReturn(List.of(dto1, dto2));
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/students"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            List<LinkedHashMap> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), List.class);
            then(service).should().fetchAll();
            assertThat(actual).isNotNull().hasSize(2);
            var actualStudent1 = actual.get(0);
            var actualFieldOfStudy1 = (LinkedHashMap) actualStudent1.get("fieldOfStudy");
            var actualStudent2 = actual.get(1);
            var actualFieldOfStudy2 = (LinkedHashMap) actualStudent2.get("fieldOfStudy");
            assertThat(actualStudent1).as("Check student1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected1.getId().intValue()),
                            entry("firstName", expected1.getFirstName()),
                            entry("lastName", expected1.getLastName()),
                            entry("email", expected1.getEmail()),
                            entry("age", expected1.getAge())
                    ));
            assertThat(actualFieldOfStudy1).as("Check student1's fieldOfStudy properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", fieldOfStudy1.getId().intValue()),
                            entry("name", fieldOfStudy1.getName()),
                            entry("description", fieldOfStudy1.getDescription())
                    ));
            assertThat(actualStudent2).as("Check student2 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected2.getId().intValue()),
                            entry("firstName", expected2.getFirstName()),
                            entry("lastName", expected2.getLastName()),
                            entry("email", expected2.getEmail()),
                            entry("age", expected2.getAge())
                    ));
            assertThat(actualFieldOfStudy2).as("Check student2's fieldOfStudy properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", fieldOfStudy2.getId().intValue()),
                            entry("name", fieldOfStudy2.getName()),
                            entry("description", fieldOfStudy2.getDescription())
                    ));
        }

        @Test
        void returns404_whenNoStudentsInDatabase() throws Exception {
            //given
            given(service.fetchAll()).willReturn(List.of());
            //when
            mockMvc.perform(get("/api/students"))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchAll();
        }
    }

    @Nested
    class CreateStudent {
        @Test
        void returns201_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            given(service.create(any(StudentDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(any(StudentDto.class));
            StudentDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), StudentDto.class);
            assertAll("Student properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s %s", "student", "first name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s %s", "student", "last name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s %s", "student", "ager").isEqualTo(expected.getAge()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s %s", "student", "email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "student", "field of study").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "student", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            given(service.create(any(StudentDto.class))).willReturn(null);
            //when
            mockMvc.perform(post("/api/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(any(StudentDto.class));
        }

        @Test
        void returns400_withErrorMsg_givenEmptyDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForFirstNameEmpty = "First name cannot be empty.";
            String expectedErrorMsgForLastNameEmpty = "Last name cannot be empty.";
            String expectedErrorMsgForAge = "Student must be at lest 18 years old.";
            String expectedErrorMsgForEmail = "Email cannot be empty.";
            Student expected = new Student();
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/students")
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
            String expectedErrorMsgForAge = "This Student is too old. Max age is 50.";
            String expectedErrorMsgForEmail = "Enter valid email address.";
            Student expected = new Student();
            expected.setFirstName("too long first name ssssssssssssssssssssssss");
            expected.setLastName("too long last name ssssssssssssssss");
            expected.setAge(51);
            expected.setEmail("student");
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/students")
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
    class UpdateStudent {
        @Test
        void returns200_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            given(service.update(any(StudentDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().update(any(StudentDto.class));
            StudentDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), StudentDto.class);
            assertAll("Student properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "student", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getFirstName())
                            .as("Check %s %s", "student", "first name").isEqualTo(expected.getFirstName()),
                    () -> assertThat(actual.getLastName())
                            .as("Check %s %s", "student", "last name").isEqualTo(expected.getLastName()),
                    () -> assertThat(actual.getAge())
                            .as("Check %s %s", "student", "ager").isEqualTo(expected.getAge()),
                    () -> assertThat(actual.getEmail())
                            .as("Check %s %s", "student", "email").isEqualTo(expected.getEmail()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "student", "field of study").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "student", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            StudentDto dto = mapper.map(expected, StudentDto.class);
            given(service.update(any(StudentDto.class))).willReturn(null);
            //when
            mockMvc.perform(put("/api/students")
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
            String expectedErrorMsgForAge = "Student must be at lest 18 years old.";
            String expectedErrorMsgForEmail = "Email cannot be empty.";
            Student expected = new Student();
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/students")
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
            String expectedErrorMsgForAge = "This Student is too old. Max age is 50.";
            String expectedErrorMsgForEmail = "Enter valid email address.";
            Student expected = new Student();
            expected.setFirstName("too long first name ssssssssssssssssssssssss");
            expected.setLastName("too long last name ssssssssssssssss");
            expected.setAge(51);
            expected.setEmail("student");
            StudentDto dto = mapper.map(expected, StudentDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/students")
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
    class DeleteStudent {
        @Test
        void returns202_givenCorrectId() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Student expected = initData.createStudentOne(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            mockMvc.perform(delete("/api/students/{id}", expected.getId()))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().remove(expected.getId());
        }
    }

    @Nested
    class DeleteAllStudents {
        @Test
        void returns202() throws Exception {
            mockMvc.perform(delete("/api/students"))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().removeAll();
        }
    }
}