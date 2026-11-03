package com.campusskills.dto;

import com.campusskills.entity.Role;
import com.campusskills.entity.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {
    private Long id;
    private String email;
    private String collegeDomain;
    private String rollNumber;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String branch;
    private Integer studyYear;
    private String bio;
    private String preferredMode;
    private String photoUrl;
    private Integer skillCredits;
    private Integer xp;
    private String level;
    private Integer dailyStreak;
    private LocalDate lastActiveDate;
    private Double averageRating;
    private Double reliabilityScore;
    private List<String> badges;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String email, String rollNumber, String fullName, Role role, UserStatus status, String branch, Integer studyYear, String bio, String preferredMode, String photoUrl, Integer skillCredits, Integer xp, String level, Integer dailyStreak, LocalDate lastActiveDate, Double averageRating, Double reliabilityScore, List<String> badges, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.branch = branch;
        this.studyYear = studyYear;
        this.bio = bio;
        this.preferredMode = preferredMode;
        this.photoUrl = photoUrl;
        this.skillCredits = skillCredits;
        this.xp = xp;
        this.level = level;
        this.dailyStreak = dailyStreak;
        this.lastActiveDate = lastActiveDate;
        this.averageRating = averageRating;
        this.reliabilityScore = reliabilityScore;
        this.badges = badges;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollegeDomain() {
        return collegeDomain;
    }

    public void setCollegeDomain(String collegeDomain) {
        this.collegeDomain = collegeDomain;
    }

    public String getRollNumber() {
        return this.rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
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

    public Integer getSkillCredits() {
        return this.skillCredits;
    }

    public void setSkillCredits(Integer skillCredits) {
        this.skillCredits = skillCredits;
    }

    public Integer getXp() {
        return this.xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDailyStreak() {
        return this.dailyStreak;
    }

    public void setDailyStreak(Integer dailyStreak) {
        this.dailyStreak = dailyStreak;
    }

    public LocalDate getLastActiveDate() {
        return this.lastActiveDate;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public Double getAverageRating() {
        return this.averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Double getReliabilityScore() {
        return this.reliabilityScore;
    }

    public void setReliabilityScore(Double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public List<String> getBadges() {
        return this.badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }

    public static class UserResponseBuilder {
        private Long id;
        private String email;
        private String collegeDomain;
        private String rollNumber;
        private String fullName;
        private Role role;
        private UserStatus status;
        private String branch;
        private Integer studyYear;
        private String bio;
        private String preferredMode;
        private String photoUrl;
        private Integer skillCredits;
        private Integer xp;
        private String level;
        private Integer dailyStreak;
        private LocalDate lastActiveDate;
        private Double averageRating;
        private Double reliabilityScore;
        private List<String> badges;
        private LocalDateTime createdAt;

        public UserResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserResponseBuilder collegeDomain(String collegeDomain) {
            this.collegeDomain = collegeDomain;
            return this;
        }

        public UserResponseBuilder rollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
            return this;
        }

        public UserResponseBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserResponseBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserResponseBuilder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public UserResponseBuilder studyYear(Integer studyYear) {
            this.studyYear = studyYear;
            return this;
        }

        public UserResponseBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public UserResponseBuilder preferredMode(String preferredMode) {
            this.preferredMode = preferredMode;
            return this;
        }

        public UserResponseBuilder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public UserResponseBuilder skillCredits(Integer skillCredits) {
            this.skillCredits = skillCredits;
            return this;
        }

        public UserResponseBuilder xp(Integer xp) {
            this.xp = xp;
            return this;
        }

        public UserResponseBuilder level(String level) {
            this.level = level;
            return this;
        }

        public UserResponseBuilder dailyStreak(Integer dailyStreak) {
            this.dailyStreak = dailyStreak;
            return this;
        }

        public UserResponseBuilder lastActiveDate(LocalDate lastActiveDate) {
            this.lastActiveDate = lastActiveDate;
            return this;
        }

        public UserResponseBuilder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public UserResponseBuilder reliabilityScore(Double reliabilityScore) {
            this.reliabilityScore = reliabilityScore;
            return this;
        }

        public UserResponseBuilder badges(List<String> badges) {
            this.badges = badges;
            return this;
        }

        public UserResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserResponse build() {
            UserResponse response = new UserResponse(this.id, this.email, this.rollNumber, this.fullName, this.role, this.status, this.branch, this.studyYear, this.bio, this.preferredMode, this.photoUrl, this.skillCredits, this.xp, this.level, this.dailyStreak, this.lastActiveDate, this.averageRating, this.reliabilityScore, this.badges, this.createdAt);
            response.setCollegeDomain(this.collegeDomain);
            return response;
        }
    }

}
