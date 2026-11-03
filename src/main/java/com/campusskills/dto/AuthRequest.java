package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @NotBlank(message = "Email or Roll Number is required")
    private String emailOrRollNumber;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthRequest() {}

    public AuthRequest(String emailOrRollNumber, String password) {
        this.emailOrRollNumber = emailOrRollNumber;
        this.password = password;
    }

    public String getEmailOrRollNumber() {
        return this.emailOrRollNumber;
    }

    public void setEmailOrRollNumber(String emailOrRollNumber) {
        this.emailOrRollNumber = emailOrRollNumber;
    }

    public void setLogin(String login) {
        if (this.emailOrRollNumber == null || this.emailOrRollNumber.isBlank()) {
            this.emailOrRollNumber = login;
        }
    }

    public void setEmail(String email) {
        if (this.emailOrRollNumber == null || this.emailOrRollNumber.isBlank()) {
            this.emailOrRollNumber = email;
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
