package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.User;
import com.tsystems.logistics.entities.Authority;
import com.tsystems.logistics.repository.AuthorityRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void addAuthority(User user, String role ) {
        Authority authority = new Authority();

        authority.setAuthority(role);
        authority.setUser(user);

        authorityRepository.save(authority);
    }

}
