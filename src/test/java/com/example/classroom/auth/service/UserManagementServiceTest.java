package com.example.classroom.auth.service;

import com.example.classroom.exception.EntityNotFoundException;
import com.example.classroom.exception.InvalidOldPasswordException;
import com.example.classroom.exception.UserAlreadyExistException;
import com.example.classroom.student.Student;
import com.example.classroom.student.StudentDto;
import com.example.classroom.student.StudentService;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.teacher.TeacherDto;
import com.example.classroom.teacher.TeacherService;
import com.example.classroom.test.util.UnitTestsInitData;
import com.example.classroom.user.User;
import com.example.classroom.user.UserRepository;
import com.example.classroom.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    private static final String EMAIL = "test@test.com";

    @InjectMocks
    UserManagementService underTest;

    @Mock
    UserRepository repository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    StudentService studentService;

    @Mock
    TeacherService teacherService;

    @Mock
    HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Spy
    UnitTestsInitData initData;

    @Spy
    ModelMapper mapper;

    @Nested
    class Register {
        @Test
        void createsUser_withAssociatedStudent_givenRegisterRequest_withStudentRole() {
            // Given
            final var role = UserRole.ROLE_STUDENT;
            final var request = initData.createRegisterRequest(role);
            final var expectedUser = initData.createUser(role);
            given(passwordEncoder.encode(anyString())).willReturn(expectedUser.getPassword());
            given(repository.save(any(User.class))).willReturn(expectedUser);

            // When
            final var actualUser = underTest.register(request);

            // Then
            verify(passwordEncoder).encode(request.getPasswordRequest().getPassword());
            verify(repository).save(any(User.class));
            verify(studentService).create(any(StudentDto.class));
            verifyNoInteractions(teacherService);
            assertThat(actualUser).isNotNull();
            assertAll("Check User's properties",
                () -> assertThat(actualUser.getId()).as("Check user's ID")
                    .isEqualTo(expectedUser.getId()),
                () -> assertThat(actualUser.getFirstName()).as("Check user's First Name")
                    .isEqualTo(expectedUser.getFirstName()),
                () -> assertThat(actualUser.getLastName()).as("Check user's Last Name")
                    .isEqualTo(expectedUser.getLastName()),
                () -> assertThat(actualUser.getEmail()).as("Check user's Email")
                    .isEqualTo(expectedUser.getEmail()),
                () -> assertThat(actualUser.getPassword()).as("Check user's Password")
                    .isEqualTo(expectedUser.getPassword()),
                () -> assertThat(actualUser.getRole()).as("Check user's Role")
                    .isEqualTo(expectedUser.getRole())
            );
        }

        @Test
        void createsUser_withAssociatedTeacher_givenRegisterRequest_withTeacherRole() {
            // Given
            final var role = UserRole.ROLE_TEACHER;
            final var request = initData.createRegisterRequest(role);
            final var expectedUser = initData.createUser(role);
            given(passwordEncoder.encode(anyString())).willReturn(expectedUser.getPassword());
            given(repository.save(any(User.class))).willReturn(expectedUser);

            // When
            final var actualUser = underTest.register(request);
            // Then
            verify(passwordEncoder).encode(request.getPasswordRequest().getPassword());
            verify(repository).save(any(User.class));
            verifyNoInteractions(studentService);
            verify(teacherService).create(any(TeacherDto.class));
            assertThat(actualUser).isNotNull();
            assertAll("Check User's properties",
                () -> assertThat(actualUser.getId()).as("Check user's ID")
                    .isEqualTo(expectedUser.getId()),
                () -> assertThat(actualUser.getFirstName()).as("Check user's First Name")
                    .isEqualTo(expectedUser.getFirstName()),
                () -> assertThat(actualUser.getLastName()).as("Check user's Last Name")
                    .isEqualTo(expectedUser.getLastName()),
                () -> assertThat(actualUser.getEmail()).as("Check user's Email")
                    .isEqualTo(expectedUser.getEmail()),
                () -> assertThat(actualUser.getPassword()).as("Check user's Password")
                    .isEqualTo(expectedUser.getPassword()),
                () -> assertThat(actualUser.getRole()).as("Check user's Role")
                    .isEqualTo(expectedUser.getRole())
            );
        }

        @Test
        void createsUser_withAssociatedTeacher_givenRegisterRequest_withDeanRole() {
            // Given
            final var role = UserRole.ROLE_DEAN;
            final var request = initData.createRegisterRequest(role);
            final var expectedUser = initData.createUser(role);
            given(passwordEncoder.encode(anyString())).willReturn(expectedUser.getPassword());
            given(repository.save(any(User.class))).willReturn(expectedUser);

            // When
            final var actualUser = underTest.register(request);

            // Then
            verify(passwordEncoder).encode(request.getPasswordRequest().getPassword());
            verify(repository).save(any(User.class));
            verifyNoInteractions(studentService);
            verify(teacherService).create(any(TeacherDto.class));
            assertThat(actualUser).isNotNull();
            assertAll("Check User's properties",
                () -> assertThat(actualUser.getId()).as("Check user's ID")
                    .isEqualTo(expectedUser.getId()),
                () -> assertThat(actualUser.getFirstName()).as("Check user's First Name")
                    .isEqualTo(expectedUser.getFirstName()),
                () -> assertThat(actualUser.getLastName()).as("Check user's Last Name")
                    .isEqualTo(expectedUser.getLastName()),
                () -> assertThat(actualUser.getEmail()).as("Check user's Email")
                    .isEqualTo(expectedUser.getEmail()),
                () -> assertThat(actualUser.getPassword()).as("Check user's Password")
                    .isEqualTo(expectedUser.getPassword()),
                () -> assertThat(actualUser.getRole()).as("Check user's Role")
                    .isEqualTo(expectedUser.getRole())
            );
        }

        @Test
        void createsUser_withoutAssociatedEntity_givenRegisterRequest_withAdminRole() {
            // Given
            final var role = UserRole.ROLE_ADMIN;
            final var request = initData.createRegisterRequest(role);
            final var expectedUser = initData.createUser(role);
            given(passwordEncoder.encode(anyString())).willReturn(expectedUser.getPassword());
            given(repository.save(any(User.class))).willReturn(expectedUser);

            // When
            final var actualUser = underTest.register(request);

            // Then
            verify(passwordEncoder).encode(request.getPasswordRequest().getPassword());
            verify(repository).save(any(User.class));
            verifyNoInteractions(studentService);
            verifyNoInteractions(teacherService);
            assertThat(actualUser).isNotNull();
            assertAll("Check User's properties",
                () -> assertThat(actualUser.getId()).as("Check user's ID")
                    .isEqualTo(expectedUser.getId()),
                () -> assertThat(actualUser.getFirstName()).as("Check user's First Name")
                    .isEqualTo(expectedUser.getFirstName()),
                () -> assertThat(actualUser.getLastName()).as("Check user's Last Name")
                    .isEqualTo(expectedUser.getLastName()),
                () -> assertThat(actualUser.getEmail()).as("Check user's Email")
                    .isEqualTo(expectedUser.getEmail()),
                () -> assertThat(actualUser.getPassword()).as("Check user's Password")
                    .isEqualTo(expectedUser.getPassword()),
                () -> assertThat(actualUser.getRole()).as("Check user's Role")
                    .isEqualTo(expectedUser.getRole())
            );
        }

        @Test
        void throwsUserAlreadyExistException_whenEmailExists_inDatabase(){
            // Given
            final var role = UserRole.ROLE_ADMIN;
            final var registerRequest = initData.createRegisterRequest(role);
            final var user = initData.createUser(role);
            final var email = user.getEmail();
            given(repository.findByEmail(email)).willReturn(Optional.of(user));

            // When, Then
            assertThatThrownBy(() -> underTest.register(registerRequest))
                .isExactlyInstanceOf(UserAlreadyExistException.class)
                .hasMessage("There is already an account with email address: " + email);
            verifyNoInteractions(passwordEncoder);
            verify(repository).findByEmail(email);
            verifyNoMoreInteractions(repository);
            verifyNoInteractions(studentService);
            verifyNoInteractions(teacherService);
        }
    }

    @Nested
    class Update {
        @Test
        void updatesUser_givenExistingUsername() {
            // Given
            final var user = initData.createUser(null);
            final var updateRequest = initData.createUpdateRequest();
            final var email = user.getEmail();
            final var expected = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(updateRequest.firstName())
                .lastName(updateRequest.lastName())
                .password(updateRequest.password())
                .build();
            given(repository.findByEmail(email)).willReturn(Optional.of(user));
            given(repository.save(any(User.class))).willReturn(expected);

            // When
            final var updated = underTest.update(updateRequest);

            // Then
            verify(repository).findByEmail(email);
            verify(repository).save(any(User.class));
            assertThat(updated).isNotNull();
            assertAll("Check User's properties",
                () -> assertThat(updated.getFirstName()).as("Check user's First Name")
                    .isEqualTo(updateRequest.firstName()),
                () -> assertThat(updated.getLastName()).as("Check user's Last Name")
                    .isEqualTo(updateRequest.lastName()),
                () -> assertThat(updated.getEmail()).as("Check user's Email")
                    .isEqualTo(updateRequest.email()),
                () -> assertThat(updated.getPassword()).as("Check user's Password")
                    .isEqualTo(updateRequest.password())
            );

        }
    }

    @Nested
    class LoadUserByUsername {
        @Test
        void returnsUser_whenUserExists_inDatabase() {
            // Given
            final var expectedUser = initData.createUser(UserRole.ROLE_STUDENT);
            final var email = expectedUser.getEmail();
            given(repository.findByEmail(email)).willReturn(Optional.of(expectedUser));

            // When
            final var actualUser = underTest.loadUserByUsername(email);

            // Then
            verify(repository).findByEmail(email);
            assertThat(actualUser).isNotNull().isEqualTo(expectedUser);
        }

        @Test
        void throwsUsernameNotFoundException_whenUserDoesNotExist_inDatabase() {
            // Given
            final var email = initData.createUser(null).getEmail();
            given(repository.findByEmail(email)).willReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> underTest.loadUserByUsername(email))
                .isExactlyInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with email " + email + " does not exist in database.");
            verify(repository).findByEmail(email);
        }
    }

    @Nested
    class RemoveByUsername {
        @Test
        void removesUser_givenExistingUsername() {
            // Given
            final var user = initData.createUser(null);
            final var email = user.getEmail();
            given(repository.findByEmail(email)).willReturn(Optional.of(user));

            // When
            underTest.removeByUsername(email);

            // Then
            verify(repository).findByEmail(email);
            verify(repository).delete(user);
        }
    }

    @Nested
    class RemoveById {
        @Test
        void removesUser_andAssignedStudent_givenExistingId() {
            // Given
            final var student = new Student();
            final var studentId = 10L;
            student.setId(studentId);
            final var user = User.builder()
                .id(1L)
                .firstName("Andrzej")
                .lastName("Nowak")
                .role(UserRole.ROLE_STUDENT)
                .student(student)
                .password("encodedPassword")
                .email("andrzej.nowak@gmail.com")
                .enabled(true)
                .build();
            final var userId = user.getId();
            given(repository.findById(userId)).willReturn(Optional.of(user));

            // When
            underTest.removeById(userId);

            // Then
            verify(studentService).remove(studentId);
            verify(repository).findById(userId);
            verify(repository).delete(user);
        }

        @Test
        void removesUser_andAssignedTeacher_givenExistingId() {
            // Given
            final var teacher = new Teacher();
            final var teacherId = 10L;
            teacher.setId(teacherId);
            final var user = User.builder()
                .id(1L)
                .firstName("Andrzej")
                .lastName("Nowak")
                .role(UserRole.ROLE_STUDENT)
                .teacher(teacher)
                .password("encodedPassword")
                .email("andrzej.nowak@gmail.com")
                .enabled(true)
                .build();
            final var userId = user.getId();
            given(repository.findById(userId)).willReturn(Optional.of(user));

            // When
            underTest.removeById(userId);

            // Then
            verify(teacherService).remove(teacherId);
            verify(repository).findById(userId);
            verify(repository).delete(user);
        }


        @Test
        void throwsEntityNotFoundException_whenIdDoesNotExist_inDatabase() {
            // Given
            final var userId = 10L;
            given(repository.findById(userId)).willReturn(Optional.empty());

            // When, Then
            assertThatThrownBy(() -> underTest.removeById(userId))
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("User was not found for parameters {ID=10}");
            verifyNoInteractions(teacherService);
            verify(repository).findById(userId);
        }
    }

    @Nested
    class UpdateUserPassword {
        @Test
        void updatesUserPassword_givenNewPassword() {
            // Given
            final var user = initData.createUser(UserRole.ROLE_STUDENT);
            final var newPassword = "newPassword";

            // When
            underTest.updateUserPassword(user, newPassword);

            // Then
            verify(passwordEncoder).encode(newPassword);
            verify(repository).save(user);
        }
    }

    @Nested
    class UpdateUserEmail {
        @Test
        void updatesUserEmail_givenNewEmail() {
            // Given
            final var user = initData.createUser(UserRole.ROLE_STUDENT);
            final var newEmail = "example@example.com";

            // When
            underTest.updateUserEmail(user, newEmail);

            // Then
            verify(repository).save(user);
        }
    }

    @Nested
    class EmailExists {
        @Test
        void returnsTrue_whenUserExists_inDatabase() {
            // Given
            final var user = initData.createUser(UserRole.ROLE_STUDENT);
            final var email = user.getEmail();
            given(repository.findByEmail(email)).willReturn(Optional.of(user));

            // When
            final var result = underTest.emailExists(email);

            // Then
            verify(repository).findByEmail(email);
            assertThat(result).isTrue();
        }

        @Test
        void returnsFalse_whenUserDoesNotExist_inDatabase() {
            // Given
            given(repository.findByEmail(any())).willReturn(Optional.empty());

            // When
            final var result = underTest.emailExists(EMAIL);

            // Then
            verify(repository).findByEmail(EMAIL);
            assertThat(result).isFalse();
        }
    }

    @Nested
    class InvalidateSession {
        @Test
        void invalidatesSession_givenValidRequest() {
            // Given
            given(request.getSession()).willReturn(session);

            // When
            underTest.invalidateSession(request);

            // Then
            verify(request).getSession();
            verify(session).invalidate();
        }

        @Test
        void doesNothing_givenNullSession() {
            // Given
            given(request.getSession()).willReturn(null);

            // When
            underTest.invalidateSession(request);

            // Then
            verify(request).getSession();
            verifyNoInteractions(session);
        }
    }

    @Nested
    class ValidateOldPassword {
        @Test
        void doesNothing_whenOldInputPasswordIsValid() {
            // Given
            final var encodedPassword = "$2y$12$JwJGKHMtlmwVbgSQoMWeGejYlt2IVn.l6Zv.vv/DsUhzgGWtFBYs6";
            final var inputPassword = "correctPassword";
            given(passwordEncoder.matches(inputPassword, encodedPassword)).willReturn(true);

            // When
            underTest.validateOldInputPassword(inputPassword, encodedPassword);

            // Then
            verify(passwordEncoder).matches(inputPassword, encodedPassword);
        }

        @Test
        void throwsException_whenOldInputPasswordIsInvalid() {
            // Given
            final var encodedPassword = "$2y$12$JwJGKHMtlmwVbgSQoMWeGejYlt2IVn.l6Zv.vv/DsUhzgGWtFBYs6";
            final var inputPassword = "wrongPassword";
            given(passwordEncoder.matches(inputPassword, encodedPassword)).willReturn(false);

            // When, Then
            assertThatThrownBy(() -> underTest.validateOldInputPassword(inputPassword, encodedPassword))
                .isExactlyInstanceOf(InvalidOldPasswordException.class)
                .hasMessage("Invalid old password!");
            verify(passwordEncoder).matches(inputPassword, encodedPassword);
        }
    }
}