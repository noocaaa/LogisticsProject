package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.User;
import com.tsystems.logistics.repository.UserRepository;
import com.tsystems.logistics.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    Set<GrantedAuthority> grantedAuthorities = user.getAuthorities()
                            .stream()
                            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                            .collect(Collectors.toSet());

                    return new UserPrincipal(user);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
    }

}
