package com.tsystems.logistics;

import com.tsystems.logistics.entities.User;
import com.tsystems.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

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

        /*List<String> driverUsernames = Arrays.asList(
                "123456789A", "987654321B", "567890123C", "345678901D",
                "12345156", "123", "456", "789"
        );

        // Generar y guardar usuarios para cada conductor
        driverUsernames.forEach(username -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("password")); // Reemplazar "password" con una contraseña segura
            user.setEnabled(true);

            userRepository.save(user);
        });
         */
    }
}
