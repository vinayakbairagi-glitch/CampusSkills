package com.campusskills.repository;

import com.campusskills.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);
    List<UserSkill> findBySkillIdAndSkillType(Long skillId, String skillType);
    List<UserSkill> findByUserIdAndSkillType(Long userId, String skillType);
    List<UserSkill> findBySkillType(String skillType);
    void deleteByUserIdAndSkillId(Long userId, Long skillId);
    boolean existsByUserIdAndSkillIdAndSkillType(Long userId, Long skillId, String skillType);
}
