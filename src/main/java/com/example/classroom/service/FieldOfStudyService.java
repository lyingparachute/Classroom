package com.example.classroom.service;

import com.example.classroom.dto.DepartmentDto;
import com.example.classroom.dto.FieldOfStudyDto;
import com.example.classroom.entity.FieldOfStudy;
import com.example.classroom.entity.Student;
import com.example.classroom.entity.Subject;
import com.example.classroom.enums.AcademicTitle;
import com.example.classroom.enums.LevelOfEducation;
import com.example.classroom.enums.Semester;
import com.example.classroom.repository.FieldOfStudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Map.entry;

@Service
@RequiredArgsConstructor
public class FieldOfStudyService {
    private final FieldOfStudyRepository repository;
    private final ModelMapper mapper;

    @Transactional
    public FieldOfStudyDto create(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = mapper.map(dto, FieldOfStudy.class);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    public List<FieldOfStudyDto> fetchAll() {
        List<FieldOfStudy> all = repository.findAll();
        return all.stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    private static Sort getSortOrder(String sortField, String sortDirection) {
        return sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
    }

    public FieldOfStudyDto fetchById(Long id) {
        Optional<FieldOfStudy> byId = repository.findById(id);
        return byId.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
    }

    @Transactional
    public void remove(Long id) {
        FieldOfStudy fieldOfStudy = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study ID: " + id));
        removeReferencingObjects(fieldOfStudy);
        repository.delete(fieldOfStudy);
    }

    @Transactional
    public void removeAll() {
        repository.findAll().forEach(this::removeReferencingObjects);
        repository.deleteAll();
    }

    public List<FieldOfStudyDto> findByName(String searched) {
        List<FieldOfStudy> found = repository.findAllByNameContainingIgnoreCase(searched);
        return found.stream().map(s -> mapper.map(s, FieldOfStudyDto.class)).toList();
    }

    public Page<FieldOfStudyDto> findByNamePaginated(int pageNo,
                                                     int pageSize,
                                                     String sortField,
                                                     String sortDirection,
                                                     String searched) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAllByNameContainingIgnoreCase(searched, pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    public Page<FieldOfStudyDto> fetchAllPaginated(int pageNo,
                                                   int pageSize,
                                                   String sortField,
                                                   String sortDirection) {
        Sort sort = getSortOrder(sortField, sortDirection);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<FieldOfStudy> all = repository.findAll(pageable);
        return all.map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class));
    }

    public Map<Semester, List<Subject>> fetchAllSubjectsFromFieldOfStudyGroupedBySemesters(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Map.ofEntries(
                entry(Semester.FIRST, filterSubjectsBySemester(subjects, Semester.FIRST).toList()),
                entry(Semester.SECOND, filterSubjectsBySemester(subjects, Semester.SECOND).toList()),
                entry(Semester.THIRD, filterSubjectsBySemester(subjects, Semester.THIRD).toList()),
                entry(Semester.FOURTH, filterSubjectsBySemester(subjects, Semester.FOURTH).toList()),
                entry(Semester.FIFTH, filterSubjectsBySemester(subjects, Semester.FIFTH).toList()),
                entry(Semester.SIXTH, filterSubjectsBySemester(subjects, Semester.SIXTH).toList()),
                entry(Semester.SEVENTH, filterSubjectsBySemester(subjects, Semester.SEVENTH).toList())
        );
    }

    public Map<Semester, Integer> calculateHoursInEachSemesterFromFieldOfStudy(Long fieldOfStudyId) {
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(fieldOfStudyId);
        return Map.ofEntries(
                entry(Semester.FIRST, filterSubjectsBySemester(subjects, Semester.FIRST)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SECOND, filterSubjectsBySemester(subjects, Semester.SECOND)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.THIRD, filterSubjectsBySemester(subjects, Semester.THIRD)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.FOURTH, filterSubjectsBySemester(subjects, Semester.FOURTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.FIFTH, filterSubjectsBySemester(subjects, Semester.FIFTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SIXTH, filterSubjectsBySemester(subjects, Semester.SIXTH)
                        .mapToInt(Subject::getHoursInSemester).sum()),
                entry(Semester.SEVENTH, filterSubjectsBySemester(subjects, Semester.SEVENTH)
                        .mapToInt(Subject::getHoursInSemester).sum())
        );
    }

    private Stream<Subject> filterSubjectsBySemester(List<Subject> subjects, Semester semester) {
        return subjects.stream().filter(s -> s.getSemester().equals(semester));
    }

    public List<String> splitDescription(Long id) {
        String description = fetchById(id).getDescription();
        if (description != null) {
            return Stream.of(description.split(";"))
                    .map(String::strip)
                    .toList();
        }
        return List.of();
    }

    @Transactional
    public FieldOfStudyDto update(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeDepartment(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    @Transactional
    public FieldOfStudyDto updateSubjects(FieldOfStudyDto dto) {
        FieldOfStudy fieldOfStudy = repository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Field Of Study '" + dto + "' with ID: " + dto.getId()));
        removeSubjects(fieldOfStudy);
        mapper.map(dto, fieldOfStudy);
        addReferencingObjects(fieldOfStudy);
        FieldOfStudy saved = repository.save(fieldOfStudy);
        return mapper.map(saved, FieldOfStudyDto.class);
    }

    public Map<Semester, Integer> calculateEctsPointsForEachSemester(Long id) {
        Map<Semester, Integer> resultMap = new EnumMap<>(Semester.class);
        List<Subject> subjects = repository.findAllSubjectsFromFieldOfStudy(id);
        Arrays.stream(Semester.values())
                .forEach(semester -> resultMap.put(semester, getSumOfEctsPointsForSemester(subjects, semester)));
        return resultMap;
    }

    private Integer getSumOfEctsPointsForSemester(List<Subject> subjects, Semester semester) {
        return subjects.stream().filter(subject -> subject.getSemester().equals(semester))
                .mapToInt(Subject::getEctsPoints)
                .sum();
    }

    public Integer getSumOfEctsPointsFromAllSemesters(Long id) {
        return calculateEctsPointsForEachSemester(id).values()
                .stream().mapToInt(Integer::intValue)
                .sum();
    }


    private void addReferencingObjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = new HashSet<>(fieldOfStudy.getSubjects());
        Set<Student> students = new HashSet<>(fieldOfStudy.getStudents());
        fieldOfStudy.setDepartment(fieldOfStudy.getDepartment());
        subjects.forEach(fieldOfStudy::addSubject);
        students.forEach(fieldOfStudy::addStudent);
    }

    public int getNumberOfSemesters(Long id) {
        AcademicTitle title = fetchById(id).getTitle();
        switch (title) {
            case BACH -> {
                return 6;
            }
            case ENG -> {
                return 7;
            }
            default -> {
                return 3;
            }
        }
    }

    public String getImagePath(Long id) {
        String imageName = fetchById(id).getImage();
        Path imagePath = Path.of("/img").resolve("fields-of-study");
        return imagePath.resolve(Objects.requireNonNullElse(imageName, "default.jpg")).toString();
    }

    public Map<LevelOfEducation, List<FieldOfStudyDto>> fetchAllGroupedByLevelOfEducation() {
        return Map.ofEntries(
                entry(LevelOfEducation.FIRST, fetchAllByLevelOfEducationSortedByName(LevelOfEducation.FIRST)),
                entry(LevelOfEducation.SECOND, fetchAllByLevelOfEducationSortedByName(LevelOfEducation.SECOND))
        );
    }

    public List<FieldOfStudyDto> fetchAllByLevelOfEducationSortedByName(LevelOfEducation levelOfEducation) {
        return repository.findAllByLevelOfEducation(levelOfEducation, Sort.by(Sort.Direction.ASC, "name"))
                .stream().map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)).toList();
    }

    public Map<String, List<FieldOfStudyDto>> fetchAllGroupedByNameAndSortedByName() {
        List<String> uniqueNames = repository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream().map(FieldOfStudy::getName).distinct().toList();
        Map<String, List<FieldOfStudyDto>> map = new LinkedHashMap<>();
        for (String name : uniqueNames) {
            map.put(name, repository.findAllByNameContainingIgnoreCase(name)
                    .stream().map(f -> mapper.map(f, FieldOfStudyDto.class)).toList());
        }
        return map;
    }

    private void removeReferencingObjects(FieldOfStudy fieldOfStudy) {
        removeDepartment(fieldOfStudy);
        removeSubjects(fieldOfStudy);
        removeStudents(fieldOfStudy);
    }

    private void removeDepartment(FieldOfStudy fieldOfStudy) {
        fieldOfStudy.setDepartment(null);
    }

    private void removeSubjects(FieldOfStudy fieldOfStudy) {
        Set<Subject> subjects = fieldOfStudy.getSubjects();
        subjects.forEach(fieldOfStudy::removeSubject);
    }

    private void removeStudents(FieldOfStudy fieldOfStudy) {
        Set<Student> students = fieldOfStudy.getStudents();
        students.forEach(fieldOfStudy::removeStudent);
    }

    public List<FieldOfStudyDto> fetchAllWithNoDepartment() {
        return repository.findAll().stream()
                .filter(fieldOfStudy -> fieldOfStudy.getDepartment() == null)
                .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
                .toList();
    }

    public List<FieldOfStudyDto> fetchAllWithGivenDepartmentDtoOrNoDepartment(DepartmentDto department) {
        return Stream.concat(
                repository.findAll().stream()
                        .filter(fieldOfStudy -> fieldOfStudy.getDepartment() == null)
                        .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class)),
                repository.findAll().stream()
                        .filter(fieldOfStudy -> fieldOfStudy.getDepartment().getId().equals(department.getId()))
                        .map(fieldOfStudy -> mapper.map(fieldOfStudy, FieldOfStudyDto.class))
        ).toList();
    }
}
