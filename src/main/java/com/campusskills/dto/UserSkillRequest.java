package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class UserSkillRequest {
    private Long skillId;
    private String skillName; // used if creating new skill or matching existing
    private String category;

    @NotBlank(message = "Skill type is required (CAN_TEACH or WANT_TO_LEARN)")
    private String skillType; // CAN_TEACH, WANT_TO_LEARN

    private String proficiency; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT

    public UserSkillRequest() {}

    public UserSkillRequest(Long skillId, String skillName, String category, String skillType, String proficiency) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.skillType = skillType;
        this.proficiency = proficiency;
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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
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

}
