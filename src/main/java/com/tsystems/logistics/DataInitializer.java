package com.tsystems.logistics;

import com.tsystems.logistics.entities.User;
import com.tsystems.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // User user = new User();
        // user.setUsername("jhondoe@gmail.com");
        // user.setPassword(passwordEncoder.encode("House1234")); // Codificar la contraseña
        // user.setEnabled(true);

        // userRepository.save(user);
    }
}
