package com.campusskills.dto;

public class ProfileUpdateRequest {
    private String fullName;
    private String branch;
    private Integer studyYear;
    private String bio;
    private String preferredMode;
    private String photoUrl;

    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String fullName, String branch, Integer studyYear, String bio, String preferredMode, String photoUrl) {
        this.fullName = fullName;
        this.branch = branch;
        this.studyYear = studyYear;
        this.bio = bio;
        this.preferredMode = preferredMode;
        this.photoUrl = photoUrl;
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
