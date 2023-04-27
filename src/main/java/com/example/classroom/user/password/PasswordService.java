package com.example.classroom.user.password;

import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserManagementService service;

    boolean resetPassword(String email) {
        try {
            User user = service.loadUserByUsername(email);
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
}
