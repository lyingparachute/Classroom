package com.example.classroom.api;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.model.Subject;
import com.example.classroom.model.Teacher;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.service.SubjectService;
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

@WebMvcTest(SubjectRestController.class)
class SubjectRestControllerWebMvcTest {

    @MockBean
    private SubjectService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ModelMapper mapper;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class GetSubject {
        @Test
        void returns200_withDtoInBody_givenExistingId() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            given(service.fetchById(anyLong())).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/subjects/{id}", expected.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            SubjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), SubjectDto.class);
            then(service).should().fetchById(expected.getId());
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "Subject", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns404_givenNonExistingId() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchById(id)).willThrow(new IllegalArgumentException(
                    "Invalid Sucject id: " + id));
            //when
            mockMvc.perform(get("/api/subjects/{id}", id))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchById(anyLong());
        }

        @Test
        void returns404_givenNull() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            given(service.fetchById(expected.getId())).willReturn(null);
            //when
            mockMvc.perform(get("/api/subjects/{id}", expected.getId()))
                    .andExpect(status().isNotFound())
                    .andDo(print());
            //then
            then(service).should().fetchById(anyLong());
        }
    }

    @Nested
    class GetSubjects {
        @Test
        void returns200_withListOfSubjectsInBody() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherTwo(null, List.of(), List.of());
            Teacher teacher3 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Subject expected1 = initData.createSubjectOne(fieldOfStudy1, List.of(teacher1, teacher2));
            Subject expected2 = initData.createSubjectTwo(fieldOfStudy2, List.of(teacher3));
            SubjectDto dto1 = mapper.map(expected1, SubjectDto.class);
            SubjectDto dto2 = mapper.map(expected2, SubjectDto.class);
            given(service.fetchAll()).willReturn(List.of(dto1, dto2));
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/subjects"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            List<LinkedHashMap> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), List.class);
            then(service).should().fetchAll();
            assertThat(actual).isNotNull().hasSize(2);
            var actualSubject1 = actual.get(0);
            var actualFieldOfStudy1 = (LinkedHashMap) actualSubject1.get("fieldOfStudy");
            var actualSubject2 = actual.get(1);
            var actualFieldOfStudy2 = (LinkedHashMap) actualSubject2.get("fieldOfStudy");
            assertThat(actualSubject1).as("Check subject1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected1.getId().intValue()),
                            entry("name", expected1.getName()),
                            entry("description", expected1.getDescription()),
                            entry("hoursInSemester", expected1.getHoursInSemester())
                    ));
            assertThat(actualFieldOfStudy1).as("Check subject1's fieldOfStudy properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", fieldOfStudy1.getId().intValue()),
                            entry("name", fieldOfStudy1.getName()),
                            entry("description", fieldOfStudy1.getDescription())
                    ));
            assertThat(actualSubject2).as("Check subject1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", expected2.getId().intValue()),
                            entry("name", expected2.getName()),
                            entry("description", expected2.getDescription()),
                            entry("hoursInSemester", expected2.getHoursInSemester())
                    ));
            assertThat(actualFieldOfStudy2).as("Check subject1's fieldOfStudy properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", fieldOfStudy2.getId().intValue()),
                            entry("name", fieldOfStudy2.getName()),
                            entry("description", fieldOfStudy2.getDescription())
                    ));
        }

        @Test
        void returns404_whenNoSubjectsInDatabase() throws Exception {
            //given
            given(service.fetchAll()).willReturn(List.of());
            //when
            mockMvc.perform(get("/api/subjects"))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchAll();
        }
    }

    @Nested
    class CreateSubject {
        @Test
        void returns201_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            given(service.create(any(SubjectDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/subjects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().create(dto);
            SubjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), SubjectDto.class);
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "Subject", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            given(service.create(any(SubjectDto.class))).willReturn(null);
            //when
            mockMvc.perform(post("/api/subjects")
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
            String expectedErrorMsgForECTS = "ECTS points value must be greater than 5.";
            Subject expected = new Subject();
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/subjects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameEmpty,
                            expectedErrorMsgForECTS);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForNameInvalid = "Name must be between 2 and 30 characters long.";
            String expectedErrorMsgForECTS = "ECTS points value must be lower than 60.";
            String expectedErrorMsgForDescription = "Description can not be longer than 500 characters.";
            String expectedErrorMsgForHoursInSemester = "Subject can not have move than 100 hours in one semester.";
            Subject expected = new Subject();
            expected.setName("too long name ssssssssssssssssssssssss");
            expected.setDescription("too long description sudbnsioajdaoisddikdmimd iehd uwhdwiohhhhhhhhh'" +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "jhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhjjjjjjssssssssssssssss");
            expected.setEctsPoints(61);
            expected.setHoursInSemester(101);
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(post("/api/subjects")
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
                            expectedErrorMsgForDescription,
                            expectedErrorMsgForHoursInSemester,
                            expectedErrorMsgForECTS);
        }
    }

    @Nested
    class UpdateSubject {
        @Test
        void returns200_withDtoInBody_givenCorrectDto() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            given(service.update(any(SubjectDto.class))).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/subjects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().update(dto);
            SubjectDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), SubjectDto.class);
            assertAll("Subject's properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s's %s", "Subject", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s's %s", "Subject", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getDescription())
                            .as("Check %s's %s", "Subject", "Description").isEqualTo(expected.getDescription()),
                    () -> assertThat(actual.getSemester())
                            .as("Check %s's %s", "Subject", "Semester").isEqualTo(expected.getSemester()),
                    () -> assertThat(actual.getHoursInSemester())
                            .as("Check %s's %s", "Subject", "Hours in semester").isEqualTo(expected.getHoursInSemester()),
                    () -> assertThat(actual.getFieldOfStudy())
                            .as("Check %s's %s", "Subject", "fieldOfStudy").isEqualTo(fieldOfStudy),
                    () -> assertThat(actual.getTeachers())
                            .as("Check if %s contains %s", "Subject", "teachers")
                            .contains(teacher1, teacher2)
            );
        }

        @Test
        void returns400_givenNullFromService() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            given(service.update(any(SubjectDto.class))).willReturn(null);
            //when
            mockMvc.perform(put("/api/subjects")
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
            String expectedErrorMsgForECTS = "ECTS points value must be greater than 5.";
            Subject expected = new Subject();
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/subjects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn();
            //then
            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).as("Check error message")
                    .contains(expectedHttpStatusCodeAsString,
                            expectedErrorMsgForNameEmpty,
                            expectedErrorMsgForECTS);
        }

        @Test
        void returns400_withErrorMsg_givenInvalidDtoFields() throws Exception {
            //given
            String expectedHttpStatusCodeAsString = String.valueOf(HttpStatus.BAD_REQUEST.value());
            String expectedErrorMsgForNameInvalid = "Name must be between 2 and 30 characters long.";
            String expectedErrorMsgForECTS = "ECTS points value must be lower than 60.";
            String expectedErrorMsgForDescription = "Description can not be longer than 500 characters.";
            String expectedErrorMsgForHoursInSemester = "Subject can not have move than 100 hours in one semester.";
            Subject expected = new Subject();
            expected.setName("too long name ssssssssssssssssssssssss");
            expected.setDescription("too long description sudbnsioajdaoisddikdmimd iehd uwhdwiohhhhhhhhh'" +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "hhhhhhhhhhhhhhhhjjjjjjjjjjjjjjjjjjjjjjjjjjjjjhhhhhhhhhhhuiiiiiiiwwwwwwwwwwwwwwwwwwwww " +
                    "jhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhjjjjjjssssssssssssssss");
            expected.setEctsPoints(61);
            expected.setHoursInSemester(101);
            SubjectDto dto = mapper.map(expected, SubjectDto.class);
            //when
            MvcResult mvcResult = mockMvc.perform(put("/api/subjects")
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
                            expectedErrorMsgForDescription,
                            expectedErrorMsgForHoursInSemester,
                            expectedErrorMsgForECTS);
        }
    }

    @Nested
    class DeleteSubject {
        @Test
        void returns202_givenCorrectId() throws Exception {
            //given
            Teacher teacher1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher teacher2 = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy = initData.createFieldOfStudyOne(null, List.of(), List.of());
            Subject expected = initData.createSubjectOne(fieldOfStudy, List.of(teacher1, teacher2));
            //when
            mockMvc.perform(delete("/api/subjects/{id}", expected.getId()))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().remove(expected.getId());
        }
    }

    @Nested
    class DeleteAllSubjects {
        @Test
        void returns202() throws Exception {
            //when
            mockMvc.perform(delete("/api/subjects"))
                    .andExpect(status().isAccepted())
                    .andDo(print());
            //then
            then(service).should().removeAll();
        }
    }
}