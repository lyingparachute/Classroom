package com.example.classroom.department;

import com.example.classroom.exception.DepartmentNotFoundException;
import com.example.classroom.fieldOfStudy.FieldOfStudy;
import com.example.classroom.pageable.PageableRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final ModelMapper mapper;

    @Transactional
    DepartmentDto create(final DepartmentDto dto) {
        final var department = mapper.map(dto, Department.class);
        addReferencingObjects(department);
        final var saved = repository.save(department);
        return mapper.map(saved, DepartmentDto.class);
    }

    @Transactional
    DepartmentDto update(final DepartmentDto dto) {
        final var department = repository.findById(dto.getId())
            .orElseThrow(() -> new DepartmentNotFoundException(
                "Invalid Department '" + dto.getName() + "' with ID: " + dto.getId()));
        removeReferencingObjects(department);
        mapper.map(dto, department);
        addReferencingObjects(department);
        return mapper.map(department, DepartmentDto.class);
    }

    public List<DepartmentDto> fetchAll() {
        final var departments = repository.findAll();
        return departments.stream().map(
            department -> mapper.map(department, DepartmentDto.class)).toList();
    }

    Page<DepartmentDto> fetchAllPaginated(final int pageNo,
                                          final int pageSize,
                                          final String sortField,
                                          final String sortDirection) {
        final var sort = getSortOrder(sortField, sortDirection);
        final Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        final var all = repository.findAll(pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }

    DepartmentDto fetchById(final Long id) {
        final var byId = repository.findById(id);
        return byId.map(department -> mapper.map(department, DepartmentDto.class))
            .orElseThrow(() -> new DepartmentNotFoundException(
                "Invalid Department id: " + id));
    }

    @Transactional
    void remove(final Long id) {
        final var department = repository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException(
                "Invalid Department id: " + id));
        removeReferencingObjects(department);
        repository.delete(department);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    Collection<DepartmentDto> findByName(final String searched) {
        final var found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, DepartmentDto.class)).toList();
    }

    Page<DepartmentDto> findByNamePaginated(final PageableRequest request) {
        final var sort = getSortOrder(request.sortField(), request.sortDirection());
        final Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        final var all = repository.findAllByNameContainingIgnoreCase(request.searched(), pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }

    private void addReferencingObjects(final Department department) {
        final Set<FieldOfStudy> fieldsOfStudy = new HashSet<>(department.getFieldsOfStudy());
        department.setDean(department.getDean());
        fieldsOfStudy.forEach(department::addFieldOfStudy);
    }

    private void removeReferencingObjects(final Department department) {
        final Set<FieldOfStudy> fieldsOfStudy = new HashSet<>(department.getFieldsOfStudy());
        department.setDean(null);
        fieldsOfStudy.forEach(department::removeFieldOfStudy);
    }

    private Sort getSortOrder(final String sortField,
                              final String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
            Sort.by(sortField).ascending() :
            Sort.by(sortField).descending();
    }
}
