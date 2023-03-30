package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.exception.DepartmentNotFoundException;
import com.example.classroom.model.Department;
import com.example.classroom.model.FieldOfStudy;
import com.example.classroom.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public DepartmentDto create(DepartmentDto dto) {
        Department department = mapper.map(dto, Department.class);
        addReferencingObjects(department);
        Department saved = repository.save(department);
        return mapper.map(saved, DepartmentDto.class);
    }

    @Transactional
    public DepartmentDto update(DepartmentDto dto) {
        Department department = repository.findById(dto.getId())
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Invalid Department '" + dto.getName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(department);
        mapper.map(dto, department);
        addReferencingObjects(department);
        return mapper.map(department, DepartmentDto.class);
    }

    public List<DepartmentDto> fetchAll() {
        List<Department> departments = repository.findAll();
        return departments.stream().map(
                department -> mapper.map(department, DepartmentDto.class)).toList();
    }

    public Page<DepartmentDto> fetchAllPaginated(int pageNo,
                                                 int pageSize,
                                                 String sortField,
                                                 String sortDirection) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Department> all = repository.findAll(pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }

    public DepartmentDto fetchById(Long id) {
        Optional<Department> byId = repository.findById(id);
        return byId.map(department -> mapper.map(department, DepartmentDto.class))
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Invalid Department id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Invalid Department id: " + id));
        removeReferencingObjects(department);
        repository.delete(department);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<DepartmentDto> findByName(String searched) {
        List<Department> found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, DepartmentDto.class)).toList();
    }

    public Page<DepartmentDto> findByNamePaginated(int pageNo,
                                                   int pageSize,
                                                   String sortField,
                                                   String sortDirection,
                                                   String searched) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Department> all = repository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }

    private void addReferencingObjects(Department department) {
        Set<FieldOfStudy> fieldsOfStudy = new HashSet<>(department.getFieldsOfStudy());
        department.setDean(department.getDean());
        fieldsOfStudy.forEach(department::addFieldOfStudy);
    }

    private void removeReferencingObjects(Department department) {
        Set<FieldOfStudy> fieldsOfStudy = new HashSet<>(department.getFieldsOfStudy());
        department.setDean(null);
        fieldsOfStudy.forEach(department::removeFieldOfStudy);
    }

    private Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }
}
