package com.example.classroom.repository;

import com.example.classroom.entity.Department;
import com.example.classroom.repository.util.IntegrationTestsInitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DepartmentRepositoryTest {

    @Autowired
    DepartmentRepository underTest;

    @Autowired
    IntegrationTestsInitData integrationTestsInitData;

    @BeforeEach
    void setup() {
        integrationTestsInitData.cleanUp();
    }

    @Test
    void shouldFindAllByNameContainingIgnoreCase() {
        //given
        String name = "MECH";
        Department expected = new Department();
        expected.setName("Wydział mechaniczny");
        underTest.save(expected);
        //when
        List<Department> actual = underTest.findAllByNameContainingIgnoreCase(name);
        //then
        assertThat(actual).isNotNull().hasSize(1);
        Department actualDepartment = actual.get(0);
        assertThat(actualDepartment).isInstanceOf(Department.class);
        assertThat(actualDepartment.getName()).isEqualTo(expected.getName());
    }
}
