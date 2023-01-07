package com.example.classroom.dto;

import com.example.classroom.entity.Teacher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * A DTO for the {@link com.example.classroom.entity.Department} entity
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DepartmentDto implements Serializable {
    private Long id;
    private String name;
    private String address;
    @Pattern(regexp = "(\\+48|0)[0-9]{9}")
    private int telNumber;
    private Teacher dean;
}