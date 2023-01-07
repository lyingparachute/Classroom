package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.entity.Department;
import com.example.classroom.repository.DepartmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private final DepartmentRepository repository;
    private final ModelMapper mapper;

    public DepartmentService(DepartmentRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public DepartmentDto create(DepartmentDto dto) {
        Department saved = repository.save(mapper.map(dto, Department.class));
        return mapper.map(saved, DepartmentDto.class);
    }

    @Transactional
    public DepartmentDto update(DepartmentDto dto) {
        Department department = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department '" + dto + "' with ID: " + dto.getId()));
        mapper.map(dto, department);
        Department saved = repository.save(department);
        return mapper.map(saved, DepartmentDto.class);

    }

    public List<DepartmentDto> fetchAll() {
        List<Department> departments = repository.findAll();
        return departments.stream().map(department -> mapper.map(department, DepartmentDto.class)).collect(Collectors.toList());
    }

    public Page<DepartmentDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Department> all = repository.findAll(pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }


    public DepartmentDto fetchById(Long id) {
        Optional<Department> byId = repository.findById(id);
        return byId.map(department -> mapper.map(department, DepartmentDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid department id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department id: " + id));
        repository.delete(department);
    }

    public List<DepartmentDto> findByName(String searched) {
        List<Department> found = repository.findAllByName(searched);
        return found.stream().map(s -> mapper.map(s, DepartmentDto.class)).collect(Collectors.toList());
    }

    public Page<DepartmentDto> findByNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Department> all = repository.findAllByName(searched, pageable);
        return all.map(department -> mapper.map(department, DepartmentDto.class));
    }

    @Transactional
    public void removeAll() {
        repository.deleteAll();
    }
}
