package com.tsystems.logistics.service;


import com.tsystems.logistics.entities.User;
import com.tsystems.logistics.entities.Authority;
import com.tsystems.logistics.repository.UserRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addDriver(String personalID) {
        User user = new User();

        user.setUsername(personalID);
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);

        userRepository.save(user);
    }

    public Set<String> getUserRoles(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            return Collections.emptySet();
        }
        User user = userOptional.get();
        return user.getAuthorities().stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toSet());
    }
}
