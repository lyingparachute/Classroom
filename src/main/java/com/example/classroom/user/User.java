package com.example.classroom.user;

import com.example.classroom.student.Student;
import com.example.classroom.teacher.Teacher;
import com.example.classroom.token.Token;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;

    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens = new ArrayList<>();

    @OneToOne(mappedBy = "userDetails")
    @Setter(AccessLevel.NONE)
    private Student student;

    @OneToOne(mappedBy = "userDetails")
    @Setter(AccessLevel.NONE)
    private Teacher teacher;

    public User() {
        this.enabled = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isStudent() {
        return role == UserRole.ROLE_STUDENT;
    }

    public boolean isTeacher() {
        return role == UserRole.ROLE_TEACHER;
    }

    public void setAttendee(Object attendee) {
        if (role.equals(UserRole.ROLE_STUDENT))
            this.student = (Student) attendee;
        if (role.equals(UserRole.ROLE_TEACHER) || role.equals(UserRole.ROLE_DEAN))
            this.teacher = (Teacher) attendee;
    }

    public Object getAttendee() {
        return isTeacher() ? teacher : student;
    }

    public void enableAccount() {
        this.enabled = true;
    }
}
