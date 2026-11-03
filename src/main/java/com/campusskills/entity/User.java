package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String rollNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private String branch;
    private Integer studyYear;

    @Column(length = 1000)
    private String bio;

    private String preferredMode; // ONLINE, OFFLINE, BOTH
    private String photoUrl;

    private Integer skillCredits = 3;

    private Integer xp = 0;

    private String level = "Beginner";

    private Integer dailyStreak = 1;

    private LocalDate lastActiveDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (skillCredits == null) skillCredits = 3;
        if (xp == null) xp = 0;
        if (level == null) level = "Beginner";
        if (dailyStreak == null) dailyStreak = 1;
        if (lastActiveDate == null) lastActiveDate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User() {}

    public User(Long id, String email, String rollNumber, String password, String fullName, Role role, UserStatus status, String branch, Integer studyYear, String bio, String preferredMode, String photoUrl, Integer skillCredits, Integer xp, String level, Integer dailyStreak, LocalDate lastActiveDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.rollNumber = rollNumber;
        this.password = password;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String email;
        private String rollNumber;
        private String password;
        private String fullName;
        private Role role;
        private UserStatus status;
        private String branch;
        private Integer studyYear;
        private String bio;
        private String preferredMode;
        private String photoUrl;
        private Integer skillCredits = 3;
        private Integer xp = 0;
        private String level = "Beginner";
        private Integer dailyStreak = 1;
        private LocalDate lastActiveDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder rollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserBuilder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public UserBuilder studyYear(Integer studyYear) {
            this.studyYear = studyYear;
            return this;
        }

        public UserBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public UserBuilder preferredMode(String preferredMode) {
            this.preferredMode = preferredMode;
            return this;
        }

        public UserBuilder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public UserBuilder skillCredits(Integer skillCredits) {
            this.skillCredits = skillCredits;
            return this;
        }

        public UserBuilder xp(Integer xp) {
            this.xp = xp;
            return this;
        }

        public UserBuilder level(String level) {
            this.level = level;
            return this;
        }

        public UserBuilder dailyStreak(Integer dailyStreak) {
            this.dailyStreak = dailyStreak;
            return this;
        }

        public UserBuilder lastActiveDate(LocalDate lastActiveDate) {
            this.lastActiveDate = lastActiveDate;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(this.id, this.email, this.rollNumber, this.password, this.fullName, this.role, this.status, this.branch, this.studyYear, this.bio, this.preferredMode, this.photoUrl, this.skillCredits, this.xp, this.level, this.dailyStreak, this.lastActiveDate, this.createdAt, this.updatedAt);
        }
    }

}
