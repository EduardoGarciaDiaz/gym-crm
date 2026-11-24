package com.epam.gym_crm.config;

import com.epam.gym_crm.entities.Trainee;
import com.epam.gym_crm.entities.Trainer;
import com.epam.gym_crm.entities.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StorageConfig {
    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public static StorageInitializer storageInitializer() {
        return new StorageInitializer();
    }
}
