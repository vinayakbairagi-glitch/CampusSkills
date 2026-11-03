package com.campusskills.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Roll Number is required")
    private String rollNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    private String branch;
    private Integer studyYear;
    private String bio;
    private String preferredMode; // ONLINE, OFFLINE, BOTH
    private String photoUrl;

    public RegisterRequest() {}

    public RegisterRequest(String email, String rollNumber, String password, String fullName, String branch, Integer studyYear, String bio, String preferredMode, String photoUrl) {
        this.email = email;
        this.rollNumber = rollNumber;
        this.password = password;
        this.fullName = fullName;
        this.branch = branch;
        this.studyYear = studyYear;
        this.bio = bio;
        this.preferredMode = preferredMode;
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRollNumber() {
        return this.rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getStudyYear() {
        return this.studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPreferredMode() {
        return this.preferredMode;
    }

    public void setPreferredMode(String preferredMode) {
        this.preferredMode = preferredMode;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
