package com.example.classroom.repository.util;

import com.example.classroom.entity.Department;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Component
public class InitDepartment {
    private final DepartmentRepository departmentRepository;

    public InitDepartment(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public void cleanUp(){
        departmentRepository.deleteAll();
    }

    public Department createDepartmentOne(Teacher dean, List<FieldOfStudy> fieldsOfStudy){
        Department department = new Department();
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber(123456789);
        if (dean != null) {
            department.setDean(dean);
        }
        if (fieldsOfStudy != null && !fieldsOfStudy.isEmpty()) {
            department.setFieldsOfStudy(new HashSet<>(fieldsOfStudy));
        }
        return departmentRepository.save(department);
    }
}
