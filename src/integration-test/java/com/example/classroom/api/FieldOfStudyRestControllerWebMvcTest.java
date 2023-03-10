package com.example.classroom.api;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.model.*;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.service.FieldOfStudyService;
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

@WebMvcTest(FieldOfStudyRestController.class)
class FieldOfStudyRestControllerWebMvcTest {

    @MockBean
    private FieldOfStudyService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ModelMapper mapper;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class GetFieldOfStudy {
        @Test
        void returns200_withDtoInBody_givenExistingId() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            given(service.fetchById(expected.getId())).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/fields-of-study/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            FieldOfStudyDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), FieldOfStudyDto.class);
            then(service).should().fetchById(expected.getId());
            assertAll("FieldOfStudy properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "fieldOfStudy", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s %s", "fieldOfStudy", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s %s", "fieldOfStudy", "Address").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s %s", "fieldOfStudy", "Telephone Number").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s %s", "fieldOfStudy", "Telephone Number").isEqualTo(expected.getTitle()),
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "fieldOfStudy", "department").isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "fieldOfStudy", "subjects")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "fieldOfStudy", "students")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns404_givenNonExistingId() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchById(id)).willThrow(new IllegalArgumentException(
                    "Invalid FieldOfStudy id: " + id));
            //when
            mockMvc.perform(get("/api/fields-of-study/{id}", id))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchById(anyLong());
        }

        @Test
        void returns404_givenNull() throws Exception {
            //given
            Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
            given(service.fetchById(expected.getId())).willReturn(null);
            //when
            mockMvc.perform(get("/api/fields-of-study/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andDo(print());
            //then
            then(service).should().fetchById(expected.getId());
        }
    }

    @Nested
    class GetFieldsOfStudy {
        @Test
        void returns200_withListOfFieldOfStudyInBody() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Student student3 = initData.createStudentThree(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Subject subject3 = initData.createSubjectThree(null, List.of());
            Department department1 = initData.createDepartmentOne(null, List.of());
            Department department2 = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected1 = initData.createFieldOfStudyOne(department1, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudy expected2 = initData.createFieldOfStudyTwo(department2, List.of(subject3), List.of(student3));
            FieldOfStudyDto dto1 = mapper.map(expected1, FieldOfStudyDto.class);
            FieldOfStudyDto dto2 = mapper.map(expected2, FieldOfStudyDto.class);
            given(service.fetchAll()).willReturn(List.of(dto1, dto2));
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/fields-of-study"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            List<LinkedHashMap> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), List.class);
            then(service).should().fetchAll();
            assertThat(actual).isNotNull().hasSize(2);
            var actualFieldOfStudy1 = actual.get(0);
            var actualDepartment1 = (LinkedHashMap) actualFieldOfStudy1.get("department");
            var actualFieldOfStudy2 = actual.get(1);
            var actualDepartment2 = (LinkedHashMap) actualFieldOfStudy2.get("department");
            assertThat(actualFieldOfStudy1).as("Check fieldOfStudy1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected1.getId().intValue()),
                            entry("name", expected1.getName()),
                            entry("description", expected1.getDescription())
                    ));
            assertThat(actualDepartment1).as("Check fieldOfStudy1's department properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", department1.getId().intValue()),
                            entry("name", department1.getName()),
                            entry("address", department1.getAddress()),
                            entry("telNumber", department1.getTelNumber())
                    ));
            assertThat(actualFieldOfStudy2).as("Check fieldOfStudy2 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected2.getId().intValue()),
                            entry("name", expected2.getName()),
                            entry("description", expected2.getDescription())
                    ));
            assertThat(actualDepartment2).as("Check fieldOfStudy2's department properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", department2.getId().intValue()),
                            entry("name", department2.getName()),
                            entry("address", department2.getAddress()),
                            entry("telNumber", department2.getTelNumber())
                    ));
        }

        @Test
        void returns404_whenNoFieldsOfStudyInDatabase() throws Exception {
            //given
            given(service.fetchAll()).willReturn(List.of());
            //when
            mockMvc.perform(get("/api/fields-of-study"))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchAll();
        }
    }

    @Nested
    class CreateFieldOfStudy {
        @Test
        void returns201_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            given(service.create(any(FieldOfStudyDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(dto);
            FieldOfStudyDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), FieldOfStudyDto.class);
            assertAll("FieldOfStudy properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "department", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s %s", "department", "Address").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTitle()),
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "department", "fieldsOfStudy")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "department", "fieldsOfStudy")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            given(service.create(any(FieldOfStudyDto.class))).willReturn(null);
            //when
            mockMvc.perform(post("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(dto);
        }

        @Test
        void returns400_withErrorMsg_givenEmptyDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForNameEmpty = "Name cannot be empty.";
            FieldOfStudy expected = new FieldOfStudy();
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameEmpty);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForNameInvalid = "Name must be between 2 and 50 characters long.";
            String expectedErrorMsgForDescription = "Description can not be longer than 500 characters.";

            FieldOfStudy expected = new FieldOfStudy();
            expected.setName("too long name ssssssssssssssssssssssssdddddddddddddddddddddddddddddddddddd");
            expected.setDescription("too long description sudbnsioajdaoisddikdmimd iehd uwhdwiohhhhhhhhh'" +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "jhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhjjjjjjssssssssssssssss");
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameInvalid,
                            expectedErrorMsgForDescription);
        }
    }

    @Nested
    class UpdateFieldOfStudy {
        @Test
        void returns200_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            given(service.update(any(FieldOfStudyDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().update(dto);
            FieldOfStudyDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), FieldOfStudyDto.class);
            assertAll("FieldOfStudy properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "department", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getLevelOfEducation())
                            .as("Check %s %s", "department", "Address").isEqualTo(expected.getLevelOfEducation()),
                    () -> assertThat(actual.getMode())
                            .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getMode()),
                    () -> assertThat(actual.getTitle())
                            .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTitle()),
                    () -> assertThat(actual.getDepartment().getId())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(department.getId()),
                    () -> assertThat(actual.getSubjects())
                            .as("Check if %s contains %s", "department", "fieldsOfStudy")
                            .contains(subject1, subject2),
                    () -> assertThat(actual.getStudents())
                            .as("Check if %s contains %s", "department", "fieldsOfStudy")
                            .contains(student1, student2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            given(service.update(any(FieldOfStudyDto.class))).willReturn(null);
            //when
            mockMvc.perform(put("/api/fields-of-study")
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
            String expectedErrorMsgForNameEmpty = "Name cannot be empty.";

            FieldOfStudy expected = new FieldOfStudy();
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameEmpty);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForNameInvalid = "Name must be between 2 and 50 characters long.";
            String expectedErrorMsgForDescription = "Description can not be longer than 500 characters.";

            FieldOfStudy expected = new FieldOfStudy();
            expected.setName("too long name ssssssssssssssssssssssssdddddddddddddddddddddddddddddddddddd");
            expected.setDescription("too long description sudbnsioajdaoisddikdmimd iehd uwhdwiohhhhhhhhh'" +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "jhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhjjjjjjssssssssssssssss");
            FieldOfStudyDto dto = mapper.map(expected, FieldOfStudyDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/fields-of-study")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameInvalid,
                            expectedErrorMsgForDescription);
        }
    }

    @Nested
    class DeleteFieldOfStudy {
        @Test
        void returns202_givenCorrectId() throws Exception {
            //given
            Student student1 = initData.createStudentOne(null, List.of());
            Student student2 = initData.createStudentTwo(null, List.of());
            Subject subject1 = initData.createSubjectOne(null, List.of());
            Subject subject2 = initData.createSubjectTwo(null, List.of());
            Department department = initData.createDepartmentOne(null, List.of());
            FieldOfStudy expected = initData.createFieldOfStudyOne(department, List.of(subject1, subject2), List.of(student1, student2));
            //when
            mockMvc.perform(delete("/api/fields-of-study/{id}", expected.getId()))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().remove(expected.getId());
        }
    }

    @Nested
    class DeleteAllFieldsOfStudy {
        @Test
        void returns202() throws Exception {
            //when
            mockMvc.perform(delete("/api/fields-of-study"))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().removeAll();
        }
    }
}