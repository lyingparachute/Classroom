package com.example.classroom.auth.service;

import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.exception.EntityNotFoundException;
import com.example.classroom.exception.InvalidOldPasswordException;
import com.example.classroom.exception.UserAlreadyExistException;
import com.example.classroom.student.StudentDto;
import com.example.classroom.student.StudentService;
import com.example.classroom.teacher.TeacherDto;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import com.example.classroom.user.register.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
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
    public User register(final RegisterRequest request) throws UserAlreadyExistException {
        final var email = request.getEmail();
        if (emailExists(email))
            throw new UserAlreadyExistException("There is already an account with email address: " + email);
        final var user = new User();
        mapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPasswordRequest().getPassword()));
        final var savedUser = repository.save(user);
        createUniversityAttendeeAccount(request, savedUser);
        savedUser.getAttendee();
        return savedUser;
    }

    private void createUniversityAttendeeAccount(final RegisterRequest request,
                                                 final User user) {
        final var requestRole = request.getRole();
        if (requestRole == UserRole.ROLE_STUDENT) {
            final var studentDto = mapper.map(request, StudentDto.class);
            studentDto.setUserDetails(user);
            studentService.create(studentDto);
        }
        if (requestRole == UserRole.ROLE_TEACHER || requestRole == UserRole.ROLE_DEAN) {
            final var teacherDto = mapper.map(request, TeacherDto.class);
            teacherDto.setUserDetails(user);
            teacherService.create(teacherDto);
        }
    }

    @Transactional
    public User update(final UpdateRequest request) {
        final var user = loadUserByUsername(request.email());
        mapper.map(request, user);
        return repository.save(user);
    }

    @Override
    public User loadUserByUsername(final String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist in database."));
    }

    @Transactional
    public void removeByUsername(final String email) {
        repository.delete(loadUserByUsername(email));
    }

    @Transactional
    public void removeById(final Long id) {
        final var byId = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                User.class, "User with given ID does not exist in database."));
        removeUniversityAttendeeAccount(byId);
        repository.delete(byId);
    }

    public void updateUserPassword(final User user,
                                   final String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);
    }

    public void updateUserEmail(final User user,
                                final String newEmail) {
        user.setEmail(newEmail);
        repository.save(user);
    }

    private boolean emailExists(final String email) {
        return repository.findByEmail(email).isPresent();
    }

    private void removeUniversityAttendeeAccount(final User user) {
        if (user.getStudent() != null) {
            studentService.remove(user.getStudent().getId());
        }
        if (user.getTeacher() != null) {
            teacherService.remove(user.getTeacher().getId());
        }
    }

    public void invalidateSession(final HttpServletRequest request) {
        final var session = request.getSession();
        SecurityContextHolder.clearContext();
        if (session != null) {
            session.invalidate();
        }
    }

    public void validateOldPassword(final String oldPasswordInput,
                                    final String userPassword) {
        if (!passwordEncoder.matches(oldPasswordInput, userPassword))
            throw new InvalidOldPasswordException("Invalid old password!");
    }
}
