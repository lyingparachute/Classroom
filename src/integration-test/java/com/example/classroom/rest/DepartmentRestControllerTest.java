package com.example.classroom.rest;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
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

import java.util.List;

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
        void returns200_givenCorrectId() throws Exception {
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
    }
}