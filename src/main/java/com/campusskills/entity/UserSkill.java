package com.campusskills.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "user_skills")
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long skillId;

    private String skillName;

    @Column(nullable = false)
    private String skillType; // CAN_TEACH, WANT_TO_LEARN

    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    private Boolean verified = false;

    public UserSkill() {}

    public UserSkill(Long id, Long userId, Long skillId, String skillName, String skillType, String proficiency, Boolean verified) {
        this.id = id;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.skillType = skillType;
        this.proficiency = proficiency;
        this.verified = verified;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSkillId() {
        return this.skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillType() {
        return this.skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public String getProficiency() {
        return this.proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public Boolean getVerified() {
        return this.verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public static UserSkillBuilder builder() {
        return new UserSkillBuilder();
    }

    public static class UserSkillBuilder {
        private Long id;
        private Long userId;
        private Long skillId;
        private String skillName;
        private String skillType;
        private String proficiency;
        private Boolean verified = false;

        public UserSkillBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSkillBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserSkillBuilder skillId(Long skillId) {
            this.skillId = skillId;
            return this;
        }

        public UserSkillBuilder skillName(String skillName) {
            this.skillName = skillName;
            return this;
        }

        public UserSkillBuilder skillType(String skillType) {
            this.skillType = skillType;
            return this;
        }

        public UserSkillBuilder proficiency(String proficiency) {
            this.proficiency = proficiency;
            return this;
        }

        public UserSkillBuilder verified(Boolean verified) {
            this.verified = verified;
            return this;
        }

        public UserSkill build() {
            return new UserSkill(this.id, this.userId, this.skillId, this.skillName, this.skillType, this.proficiency, this.verified);
        }
    }

}
