package com.example.classroom.rest;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.exception.DepartmentNotFoundException;
import com.example.classroom.repository.util.UnitTestsInitData;
import com.example.classroom.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentRestController.class)
class DepartmentRestControllerTest {

    @MockBean
    private DepartmentService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Spy
    ModelMapper mapper;

    UnitTestsInitData initData = new UnitTestsInitData();

    @Nested
    class GetDepartment {
        @Test
        void returns200_withDepartmentInBody_givenExistingId() throws Exception {
            //given
            Teacher dean = initData.createTeacherOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Department expected = initData.createDepartmentOne(dean, List.of(fieldOfStudy1, fieldOfStudy2));
            DepartmentDto dto = mapper.map(expected, DepartmentDto.class);
            given(service.fetchById(expected.getId())).willReturn(dto);
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/departments/{id}", expected.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            DepartmentDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), DepartmentDto.class);
            then(service).should().fetchById(anyLong());
            assertAll("Department properties",
                    () -> assertThat(actual.getId())
                            .as("Check %s %s", "department", "ID").isEqualTo(expected.getId()),
                    () -> assertThat(actual.getName())
                            .as("Check %s %s", "department", "Name").isEqualTo(expected.getName()),
                    () -> assertThat(actual.getAddress())
                            .as("Check %s %s", "department", "Address").isEqualTo(expected.getAddress()),
                    () -> assertThat(actual.getTelNumber())
                            .as("Check %s %s", "department", "Telephone Number").isEqualTo(expected.getTelNumber()),
                    () -> assertThat(actual.getDean())
                            .as("Check %s's %s", "Department", "Dean").isEqualTo(dean),
                    () -> assertThat(actual.getFieldsOfStudy())
                            .as("Check if %s contains %s", "department", "fieldsOfStudy")
                            .contains(fieldOfStudy1, fieldOfStudy2)
            );
        }

        @Test
        void returns404_givenNonExistingId() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchById(id)).willThrow(new DepartmentNotFoundException(
                    "Invalid Department id: " + id));
            //when
            mockMvc.perform(get("/api/departments/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
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
            mockMvc.perform(get("/api/departments/{id}", expected.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print());
            //then
            then(service).should().fetchById(anyLong());
        }
    }

    @Nested
    class GetDepartments {
        @Test
        void returns200_withListOfDepartmentsInBody() throws Exception {
            //given
            Teacher dean1 = initData.createTeacherOne(null, List.of(), List.of());
            Teacher dean2 = initData.createTeacherThree(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy1 = initData.createFieldOfStudyOne(null, List.of(), List.of());
            FieldOfStudy fieldOfStudy2 = initData.createFieldOfStudyTwo(null, List.of(), List.of());
            Department expected1 = initData.createDepartmentOne(dean1, List.of(fieldOfStudy1));
            Department expected2 = initData.createDepartmentTwo(dean2, List.of(fieldOfStudy2));
            DepartmentDto dto1 = mapper.map(expected1, DepartmentDto.class);
            DepartmentDto dto2 = mapper.map(expected2, DepartmentDto.class);
            given(service.fetchAll()).willReturn(List.of(dto1, dto2));
            //when
            MvcResult mvcResult = mockMvc.perform(get("/api/departments")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn();
            //then
            List<LinkedHashMap> actual = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), List.class);
            then(service).should().fetchAll();
            assertThat(actual).isNotNull().hasSize(2);
            var actualDepartment1 = actual.get(0);
            var actualDean1 = (LinkedHashMap) actualDepartment1.get("dean");
            var actualDepartment2 = actual.get(1);
            var actualDean2 = (LinkedHashMap) actualDepartment2.get("dean");
            assertThat(actualDepartment1).as("Check department1 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("name", expected1.getName()),
                            entry("address", expected1.getAddress()),
                            entry("telNumber", expected1.getTelNumber())
                    ));
            assertThat(actualDean1).as("Check department1's dean properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", dean1.getId().intValue()),
                            entry("firstName", dean1.getFirstName()),
                            entry("lastName", dean1.getLastName()),
                            entry("age", dean1.getAge()),
                            entry("email", dean1.getEmail())
                    ));
            assertThat(actualDepartment2).as("Check department2 properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("name", expected2.getName()),
                            entry("address", expected2.getAddress()),
                            entry("telNumber", expected2.getTelNumber())
                    ));
            assertThat(actualDean2).as("Check department2's dean properties")
                    .containsAllEntriesOf(Map.ofEntries(
                            entry("id", dean2.getId().intValue()),
                            entry("firstName", dean2.getFirstName()),
                            entry("lastName", dean2.getLastName()),
                            entry("age", dean2.getAge()),
                            entry("email", dean2.getEmail())
                    ));
        }

        @Test
        void returns404_whenNoDepartmentsInDatabase() throws Exception {
            //given
            Long id = 100L;
            given(service.fetchAll()).willReturn(List.of());
            //when
            mockMvc.perform(get("/api/departments")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andReturn();
            //then
            then(service).should().fetchAll();
        }
    }
}