package com.epam.gym_crm.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialsGeneratorTest {

    private CredentialsGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CredentialsGenerator();
    }

    @Test
    void generateUsername_noExistingUser_returnsFirstDotLast() {
        String username = generator.generateUsername("John", "Smith", u -> false);

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void generateUsername_baseExists_appendsOne() {
        Set<String> existing = Set.of("John.Smith");

        String username = generator.generateUsername("John", "Smith", existing::contains);

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void generateUsername_baseAndFirstSerialExist_appendsTwo() {
        Set<String> existing = Set.of("John.Smith", "John.Smith1");

        String username = generator.generateUsername("John", "Smith", existing::contains);

        assertThat(username).isEqualTo("John.Smith2");
    }

    @Test
    void generateUsername_gapInSerials_usesFirstFreeSerial() {
        Set<String> existing = Set.of("John.Smith", "John.Smith2");

        String username = generator.generateUsername("John", "Smith", existing::contains);

        assertThat(username).isEqualTo("John.Smith1");
    }

    @Test
    void generateUsername_namesWithSurroundingSpaces_areTrimmed() {
        String username = generator.generateUsername("  John ", " Smith  ", u -> false);

        assertThat(username).isEqualTo("John.Smith");
    }

    @Test
    void generatePassword_hasTenAlphanumericChars() {
        String password = generator.generatePassword();

        assertThat(password).hasSize(10).matches("[A-Za-z0-9]+");
    }

    @Test
    void generatePassword_manyCalls_alwaysTenAlphanumericChars() {
        for (int i = 0; i < 200; i++) {
            assertThat(generator.generatePassword()).hasSize(10).matches("[A-Za-z0-9]+");
        }
    }

    @Test
    void generatePassword_twoCalls_returnDifferentValues() {
        assertThat(generator.generatePassword()).isNotEqualTo(generator.generatePassword());
    }
}
