package com.example.classroom.repository.util;

import com.example.classroom.entity.Department;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

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

    public Department createDepartmentOne(Teacher dean){
        Department department = new Department();
        department.setName("Wydział Elektroniki, Telekomunikacji i Informatyki");
        department.setAddress("ul. Gabriela Narutowicza 11/12 80-233 Gdańsk");
        department.setTelNumber(123456789);
        department.setDean(dean);
        return departmentRepository.save(department);
    }
}
