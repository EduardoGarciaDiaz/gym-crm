package com.epam.gym_crm.services;

import com.epam.gym_crm.dtos.Credentials;
import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.User;
import com.epam.gym_crm.exceptions.AuthenticationException;
import com.epam.gym_crm.repositories.ITraineeRepository;
import com.epam.gym_crm.repositories.ITrainerRepository;
import com.epam.gym_crm.repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    static final String INVALID_MESSAGE = "Invalid username or password";

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private ITraineeRepository traineeRepository;
    private ITrainerRepository trainerRepository;
    private IUserRepository userRepository;

    @Autowired
    public void setTraineeRepository(ITraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(ITrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean matchesTrainee(Credentials credentials) {
        return matches(credentials, u -> traineeRepository.findByUsername(u).map(Trainee::getPassword));
    }

    public boolean matchesTrainer(Credentials credentials) {
        return matches(credentials, u -> trainerRepository.findByUsername(u).map(Trainer::getPassword));
    }

    public void authenticateTrainee(Credentials credentials) {
        require(matchesTrainee(credentials), "trainee", credentials);
    }

    public void authenticateTrainer(Credentials credentials) {
        require(matchesTrainer(credentials), "trainer", credentials);
    }

    public void authenticateUser(Credentials credentials) {
        boolean ok = matches(credentials, u -> userRepository.findByUsername(u).map(User::getPassword));
        require(ok, "user", credentials);
    }

    private boolean matches(Credentials credentials, Function<String, Optional<String>> storedPasswordLookup) {
        if (credentials == null || isBlank(credentials.username()) || isBlank(credentials.password())) {
            return false;
        }
        return storedPasswordLookup.apply(credentials.username())
                .map(stored -> MessageDigest.isEqual(
                        stored.getBytes(StandardCharsets.UTF_8),
                        credentials.password().getBytes(StandardCharsets.UTF_8)))
                .orElse(false);
    }

    private void require(boolean ok, String role, Credentials credentials) {
        if (!ok) {
            log.warn("Authentication failed for {} username={}", role,
                    credentials == null ? null : credentials.username());
            throw new AuthenticationException(INVALID_MESSAGE);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
