//package com.epam.gym_crm.config;
//
//import com.epam.gym_crm.entities.Trainee;
//import com.epam.gym_crm.entities.Trainer;
//import com.epam.gym_crm.entities.Training;
//import com.epam.gym_crm.entities.TrainingType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//public class StorageInitializer implements BeanPostProcessor {
//
//    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);
//
//    @Value("${storage.init.file}")
//    private Resource dataFile;
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Object postProcessAfterInitialization(Object bean, String beanName) {
//        switch (beanName) {
//            case "traineeStorage" -> loadTrainees((Map<Long, Trainee>) bean);
//            case "trainerStorage" -> loadTrainers((Map<Long, Trainer>) bean);
//            case "trainingStorage" -> loadTrainings((Map<Long, Training>) bean);
//            default -> { }
//        }
//        return bean;
//    }
//
//    private void loadTrainees(Map<Long, Trainee> storage) {
//        for (String[] f : readRecords("TRAINEE")) {
//            Long id = Long.parseLong(f[1]);
//            storage.put(id, new Trainee(f[2], f[3], f[4], f[5], Boolean.parseBoolean(f[6]),
//                    LocalDate.parse(f[7]), f[8], id));
//        }
//        log.info("Loaded {} trainees from {}", storage.size(), dataFile);
//    }
//
//    private void loadTrainers(Map<Long, Trainer> storage) {
//        for (String[] f : readRecords("TRAINER")) {
//            Long id = Long.parseLong(f[1]);
//            storage.put(id, new Trainer(f[2], f[3], f[4], f[5], Boolean.parseBoolean(f[6]),
//                    new TrainingType(f[7]), id));
//        }
//        log.info("Loaded {} trainers from {}", storage.size(), dataFile);
//    }
//
//    private void loadTrainings(Map<Long, Training> storage) {
//        for (String[] f : readRecords("TRAINING")) {
//            Long id = Long.parseLong(f[1]);
//            storage.put(id, new Training(id, Long.parseLong(f[2]), Long.parseLong(f[3]), f[4],
//                    new TrainingType(f[5]), LocalDate.parse(f[6]), Integer.parseInt(f[7])));
//        }
//        log.info("Loaded {} trainings from {}", storage.size(), dataFile);
//    }
//
//    private List<String[]> readRecords(String type) {
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(dataFile.getInputStream(), StandardCharsets.UTF_8))) {
//            return reader.lines()
//                    .map(String::trim)
//                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
//                    .map(line -> line.split(","))
//                    .filter(fields -> fields[0].equals(type))
//                    .toList();
//        } catch (IOException e) {
//            throw new IllegalStateException("Cannot read storage init file: " + dataFile, e);
//        }
//    }
//}