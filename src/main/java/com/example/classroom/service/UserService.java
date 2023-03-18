package com.example.classroom.service;

import com.example.classroom.model.UserLogin;
import com.example.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserLogin create(UserLogin user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    @Transactional
    public UserLogin update(UserLogin user) {
        UserLogin userLogin = loadUserByUsername(user.getEmail());
        mapper.map(user, userLogin);
        return repository.save(userLogin);
    }

    @Override
    public UserLogin loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " does not exist in database."));
    }

    @Transactional
    public void removeByUsername(String email) {
        repository.delete(loadUserByUsername(email));
    }
}
