package com.example.classroom.auth.service;

import com.example.classroom.auth.model.RegisterRequest;
import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.exception.EntityNotFoundException;
import com.example.classroom.student.StudentDto;
import com.example.classroom.student.StudentService;
import com.example.classroom.teacher.TeacherDto;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserDetailsService {

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final TeacherService teacherService;

    @Transactional
    public User register(RegisterRequest request) {
        User userDetails = new User();
        mapper.map(request, userDetails);
        userDetails.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = repository.save(userDetails);
        createUniversityAttendeeAccount(request, saved);
        saved.getAttendee();
        return saved;
    }

    private void createUniversityAttendeeAccount(RegisterRequest request, User user) {
        UserRole requestRole = request.getRole();
        if (requestRole == UserRole.ROLE_STUDENT) {
            StudentDto studentDto = mapper.map(request, StudentDto.class);
            studentDto.setUserDetails(user);
            studentService.create(studentDto);
        }
        if (requestRole == UserRole.ROLE_TEACHER || requestRole == UserRole.ROLE_DEAN) {
            TeacherDto teacherDto = mapper.map(request, TeacherDto.class);
            teacherDto.setUserDetails(user);
            teacherService.create(teacherDto);
        }
    }

    @Transactional
    public User update(UpdateRequest request) {
        User userLogin = loadUserByUsername(request.getEmail());
        mapper.map(request, userLogin);
        return repository.save(userLogin);
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist in database."));
    }

    @Transactional
    public void removeByUsername(String email) {
        repository.delete(loadUserByUsername(email));
    }


    @Transactional
    public void removeById(Long id) {
        User byId = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        User.class, "User with given ID does not exist in database."));
        removeUniversityAttendeeAccount(byId);
        repository.delete(byId);
    }

    private void removeUniversityAttendeeAccount(User user) {
        if (user.getStudent() != null) {
            studentService.remove(user.getStudent().getId());
        }
        if (user.getTeacher() != null) {
            teacherService.remove(user.getTeacher().getId());
        }
    }


    public void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
    }
}
