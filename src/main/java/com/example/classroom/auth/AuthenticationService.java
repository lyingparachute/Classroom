package com.example.classroom.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public AuthenticationResponse register(RegisterRequest request) {
        return null;
    }

    public Object authenticate(AuthenticationRequest request) {
        return null;
    }
}
