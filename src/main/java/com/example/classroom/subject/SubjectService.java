package com.example.classroom.subject;

import com.example.classroom.enums.Semester;
import com.example.classroom.pageable.PageableRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository repository;
    private final ModelMapper mapper;

    @Transactional
    SubjectDto create(final SubjectDto dto) {
        final var subject = mapper.map(dto, Subject.class);
        addReferencingObjects(subject);
        final var saved = repository.save(subject);
        return mapper.map(saved, SubjectDto.class);
    }

    @Transactional
    SubjectDto update(SubjectDto dto) {
        final var subject = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject '" + dto + "' with ID: " + dto.getId()));
        removeReferencingObjects(subject);
        mapper.map(dto, subject);
        addReferencingObjects(subject);
        return mapper.map(subject, SubjectDto.class);

    }

    public List<SubjectDto> fetchAll() {
        final var subjects = repository.findAll();
        return subjects.stream().map(subject -> mapper.map(subject, SubjectDto.class)).toList();
    }

    Page<SubjectDto> fetchAllPaginated(final int pageNo,
                                       final int pageSize,
                                       final String sortField,
                                       final String sortDirection) {
        final var sort = getSortOrder(sortField, sortDirection);
        final Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        final var all = repository.findAll(pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }

    public Map<Semester, List<Subject>> fetchAllGroupedBySemesters() {
        return Map.ofEntries(
                entry(Semester.FIRST, repository.findAllBySemester(Semester.FIRST)),
                entry(Semester.SECOND, repository.findAllBySemester(Semester.SECOND)),
                entry(Semester.THIRD, repository.findAllBySemester(Semester.THIRD)),
                entry(Semester.FOURTH, repository.findAllBySemester(Semester.FOURTH)),
                entry(Semester.FIFTH, repository.findAllBySemester(Semester.FIFTH)),
                entry(Semester.SIXTH, repository.findAllBySemester(Semester.SIXTH)),
                entry(Semester.SEVENTH, repository.findAllBySemester(Semester.SEVENTH))
        );
    }

    SubjectDto fetchById(final Long id) {
        final var byId = repository.findById(id);
        return byId.map(subject -> mapper.map(subject, SubjectDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
    }

    @Transactional
    void remove(final Long id) {
        final var subject = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject id: " + id));
        removeReferencingObjects(subject);
        repository.delete(subject);
    }

    @Transactional
    void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    List<SubjectDto> findByName(final String searched) {
        final var found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, SubjectDto.class)).toList();
    }

    Page<SubjectDto> findByNamePaginated(final PageableRequest request) {
        final var sort = getSortOrder(request.sortField(), request.sortDirection());
        final Pageable pageable = PageRequest.of(request.pageNumber() - 1, request.pageSize(), sort);
        final var all = repository.findAllByNameContainingIgnoreCase(request.searched(), pageable);
        return all.map(subject -> mapper.map(subject, SubjectDto.class));
    }

    private void addReferencingObjects(Subject subject) {
        subject.setFieldOfStudy(subject.getFieldOfStudy());
        subject.getTeachers().forEach(subject::addTeacher);
    }

    private void removeReferencingObjects(Subject subject) {
        subject.setFieldOfStudy(null);
        subject.getTeachers().forEach(subject::removeTeacher);
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }
}
