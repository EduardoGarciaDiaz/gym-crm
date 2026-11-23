package com.epam.gym_crm.utils;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CredentialsGenerator {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "!@#$%^&*";

    private static final int PASSWORD_LENGTH = 10;

    private static final SecureRandom RANDOM = new SecureRandom();

    //Should it be static?
    public String generateUsername(String firstName, String lastName) {
        //TODO: Valiate if already exists, if exists, add a incremental number at the end
        String username = firstName + "." + lastName;
        return username;
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}

