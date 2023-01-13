package com.example.classroom.service;

import com.example.classroom.dto.SubjectDto;
import com.example.classroom.entity.Subject;
import com.example.classroom.entity.Teacher;
import com.example.classroom.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public SubjectDto create(SubjectDto dto) {
        Subject subject = mapper.map(dto, Subject.class);
        addReferencingObjects(subject);
        Subject saved = repository.save(subject);
        return mapper.map(saved, SubjectDto.class);
    }

    @Transactional
    public SubjectDto update(SubjectDto dto) {
        Subject subject = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject '" + dto + "' with ID: " + dto.getId()));
        removeReferencingObjects(subject);
        mapper.map(dto, subject);
        addReferencingObjects(subject);
        Subject saved = repository.save(subject);
        return mapper.map(saved, SubjectDto.class);

    }

    public List<SubjectDto> fetchAll() {
        List<Subject> subjects = repository.findAll();
        return subjects.stream().map(subject -> mapper.map(subject, SubjectDto.class)).toList();
    }

    public Page<SubjectDto> fetchAllPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = repository.findAll(pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }


    public SubjectDto fetchById(Long id) {
        Optional<Subject> byId = repository.findById(id);
        return byId.map(subject -> mapper.map(subject, SubjectDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
    }

    @Transactional
    public void remove(Long id) {
        Subject subject = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
        removeReferencingObjects(subject);
        repository.delete(subject);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<SubjectDto> findByName(String searched) {
        List<Subject> found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, SubjectDto.class)).toList();
    }

    public Page<SubjectDto> findByNamePaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searched) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Subject> all = repository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }

    private void addReferencingObjects(Subject subject) {
        Set<Teacher> teachers = new HashSet<>(subject.getTeachers());
        subject.setFieldOfStudy(subject.getFieldOfStudy());
        teachers.forEach(subject::addTeacher);
    }

    private void removeReferencingObjects(Subject subject) {
        Set<Teacher> teachers = new HashSet<>(subject.getTeachers());
        subject.setFieldOfStudy(null);
        teachers.forEach(subject::removeTeacher);
    }
}
