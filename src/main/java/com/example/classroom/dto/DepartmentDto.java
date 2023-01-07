package com.example.classroom.dto;

import com.example.classroom.entity.Teacher;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * A DTO for the {@link com.example.classroom.entity.Department} entity
 */
@Data
public class DepartmentDto implements Serializable {
    private final Long id;
    private final String name;
    private final String address;
    @Pattern(regexp = "(\\+48|0)[0-9]{9}")
    private final int telNumber;
    private final Teacher dean;
}