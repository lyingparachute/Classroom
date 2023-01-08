package com.example.classroom.service;

import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.repository.FieldOfStudyRepository;
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
public class FieldOfStudyService {
    
    private final FieldOfStudyRepository repository;
    private final ModelMapper mapper;

    public FieldOfStudyService(FieldOfStudyRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public FieldOfStudyDto create(FieldOfStudyDto dto) {
        FieldOfStudy saved = repository.save(mapper.map(dto, FieldOfStudy.class));
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    public FieldOfStudyDto update(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        mapper.map(dto, fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);

    }

    public List<FieldOfStudyDto> fetchAll() {
        List<FieldOfStudy> fieldsOfStudy = repository.findAll();
        return fieldsOfStudy.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).collect(Collectors.toList());
    }

    public Page<FieldOfStudyDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAll(pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }


    public FieldOfStudyDto fetchById(Long id) {
        Optional<FieldOfStudy> byId = repository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        FieldOfStudy fieldOfStudy = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study id: " + id));
        repository.delete(fieldOfStudy);
    }

    public List<FieldOfStudyDto> findByName(String searched) {
        List<FieldOfStudy> found = repository.findAllByName(searched);
        return found.stream().map(s -> mapper.map(s, FieldOfStudyDto.class)).collect(Collectors.toList());
    }

    public Page<FieldOfStudyDto> findByNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAllByName(searched, pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    @Transactional
    public void removeAll() {
        repository.deleteAll();
    }
}
