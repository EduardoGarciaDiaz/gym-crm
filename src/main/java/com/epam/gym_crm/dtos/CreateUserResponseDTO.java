package com.epam.gym_crm.dtos;

public class CreateUserResponseDTO {
    private String username;
    private String password;

    public CreateUserResponseDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
