package com.campusskills.service;

import com.campusskills.dto.AvailabilityRequest;
import com.campusskills.dto.UserSkillRequest;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final TextModerationService textModerationService;

    public SkillService(SkillRepository skillRepository,
                        UserSkillRepository userSkillRepository,
                        AvailabilityRepository availabilityRepository,
                        UserRepository userRepository,
                        GamificationService gamificationService,
                        TextModerationService textModerationService) {
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.textModerationService = textModerationService;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAllByOrderByNameAsc();
    }

    public List<UserSkill> getUserSkills(Long userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @Transactional
    public UserSkill addUserSkill(Long userId, UserSkillRequest req) {
        textModerationService.check(userId, "PROFILE_SKILL", req.getSkillName(), req.getCategory());
        Skill skill;
        if (req.getSkillId() != null) {
            skill = skillRepository.findById(req.getSkillId())
                    .orElseThrow(() -> new ApiException("Skill not found", HttpStatus.NOT_FOUND));
        } else if (StringUtils.hasText(req.getSkillName())) {
            String name = req.getSkillName().trim();
            skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> skillRepository.save(Skill.builder()
                            .name(name)
                            .category(req.getCategory() != null ? req.getCategory() : "GENERAL")
                            .createdBy(userId)
                            .build()));
        } else {
            throw new ApiException("Skill ID or Skill Name is required", HttpStatus.BAD_REQUEST);
        }

        String type = req.getSkillType().toUpperCase();
        if (!type.equals("CAN_TEACH") && !type.equals("WANT_TO_LEARN")) {
            throw new ApiException("Skill type must be CAN_TEACH or WANT_TO_LEARN", HttpStatus.BAD_REQUEST);
        }

        Optional<UserSkill> existing = userSkillRepository.findByUserIdAndSkillId(userId, skill.getId());
        UserSkill userSkill;
        if (existing.isPresent() && existing.get().getSkillType().equals(type)) {
            userSkill = existing.get();
            userSkill.setProficiency(req.getProficiency() != null ? req.getProficiency() : "INTERMEDIATE");
        } else {
            userSkill = UserSkill.builder()
                    .userId(userId)
                    .skillId(skill.getId())
                    .skillName(skill.getName())
                    .skillType(type)
                    .proficiency(req.getProficiency() != null ? req.getProficiency() : "INTERMEDIATE")
                    .verified(false)
                    .build();
        }

        return userSkillRepository.save(userSkill);
    }

    @Transactional
    public void deleteUserSkill(Long userId, Long userSkillId) {
        UserSkill us = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new ApiException("Skill entry not found", HttpStatus.NOT_FOUND));
        if (!us.getUserId().equals(userId)) {
            throw new ApiException("You can only remove your own skills", HttpStatus.FORBIDDEN);
        }
        userSkillRepository.delete(us);
    }

    public List<Availability> getUserAvailability(Long userId) {
        return availabilityRepository.findByUserId(userId);
    }

    @Transactional
    public List<Availability> saveAvailability(Long userId, AvailabilityRequest request) {
        availabilityRepository.deleteByUserId(userId);
        List<Availability> saved = new ArrayList<>();
        if (request.getSlots() != null) {
            for (AvailabilityRequest.Slot s : request.getSlots()) {
                if (StringUtils.hasText(s.getDayOfWeek()) && StringUtils.hasText(s.getStartTime()) && StringUtils.hasText(s.getEndTime())) {
                    Availability a = Availability.builder()
                            .userId(userId)
                            .dayOfWeek(s.getDayOfWeek().toUpperCase())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .mode(s.getMode() != null ? s.getMode() : "ONLINE")
                            .build();
                    saved.add(availabilityRepository.save(a));
                }
            }
        }
        return saved;
    }

    public List<Map<String, Object>> getSmartMatches(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        String currentCollegeDomain = extractEmailDomain(currentUser.getEmail());
        List<UserSkill> mySkills = userSkillRepository.findByUserId(currentUserId);
        Set<String> myWishList = mySkills.stream()
                .filter(s -> "WANT_TO_LEARN".equalsIgnoreCase(s.getSkillType()))
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());
        Set<String> myTeachable = mySkills.stream()
                .filter(s -> "CAN_TEACH".equalsIgnoreCase(s.getSkillType()))
                .map(s -> s.getSkillName().toLowerCase())
                .collect(Collectors.toSet());

        List<Availability> myAvailability = availabilityRepository.findByUserId(currentUserId);
        Set<String> myDays = myAvailability.stream()
                .map(a -> a.getDayOfWeek().toUpperCase())
                .collect(Collectors.toSet());

        List<User> allUsers = userRepository.findByStatus(UserStatus.VERIFIED);
        List<Map<String, Object>> matches = new ArrayList<>();

        for (User other : allUsers) {
            if (other.getId().equals(currentUserId)) continue;

            List<UserSkill> otherSkills = userSkillRepository.findByUserId(other.getId());
            List<UserSkill> otherTeachable = otherSkills.stream()
                    .filter(s -> "CAN_TEACH".equalsIgnoreCase(s.getSkillType()))
                    .collect(Collectors.toList());
            if (otherTeachable.isEmpty()) continue;
            List<UserSkill> otherWishList = otherSkills.stream()
                    .filter(s -> "WANT_TO_LEARN".equalsIgnoreCase(s.getSkillType()))
                    .collect(Collectors.toList());

            // They can teach you (their teachable skills matching my wish list)
            List<String> theyCanTeachMe = otherTeachable.stream()
                    .filter(s -> myWishList.contains(s.getSkillName().toLowerCase()))
                    .map(UserSkill::getSkillName)
                    .distinct()
                    .collect(Collectors.toList());

            // You can teach them (my teachable skills matching their wish list)
            List<String> iCanTeachThem = myTeachable.stream()
                    .filter(mySkill -> otherWishList.stream().anyMatch(ow -> ow.getSkillName().equalsIgnoreCase(mySkill)))
                    .distinct()
                    .collect(Collectors.toList());

            boolean hasVerifiedTutorTick = otherTeachable.stream().anyMatch(s -> Boolean.TRUE.equals(s.getVerified()));

            // 70% Skill coverage in both directions
            double coverage1 = myWishList.isEmpty() ? 0.0 : ((double) theyCanTeachMe.size() / myWishList.size());
            double coverage2 = otherWishList.isEmpty() ? 0.0 : ((double) iCanTeachThem.size() / otherWishList.size());
            double skillCoverage = 0.0;
            if (!myWishList.isEmpty() && !otherWishList.isEmpty()) {
                skillCoverage = ((coverage1 * 0.6) + (coverage2 * 0.4)) * 100.0;
            } else if (!myWishList.isEmpty()) {
                skillCoverage = coverage1 * 100.0;
            } else if (!otherWishList.isEmpty()) {
                skillCoverage = coverage2 * 100.0;
            } else {
                skillCoverage = 20.0; // base if no skills listed yet
            }
            skillCoverage = Math.min(100.0, skillCoverage);

            // 20% average rating
            double rating = gamificationService.calculateAverageRating(other.getId());
            double ratingScore = (rating / 5.0) * 100.0;

            // 10% overlapping availability
            List<Availability> otherAvail = availabilityRepository.findByUserId(other.getId());
            boolean overlaps = otherAvail.stream().anyMatch(oa -> myDays.contains(oa.getDayOfWeek().toUpperCase()));
            double availabilityScore = overlaps ? 100.0 : (otherAvail.isEmpty() || myAvailability.isEmpty() ? 60.0 : 20.0);

            // Total Score
            double totalScore = (0.70 * skillCoverage) + (0.20 * ratingScore) + (0.10 * availabilityScore);
            int matchScore = (int) Math.min(100, Math.round(totalScore));

            Map<String, Object> match = new HashMap<>();
            match.put("user", Map.of(
                    "id", other.getId(),
                    "fullName", other.getFullName(),
                    "email", other.getEmail(),
                    "collegeDomain", extractEmailDomain(other.getEmail()),
                    "branch", other.getBranch() != null ? other.getBranch() : "CSE",
                    "studyYear", other.getStudyYear() != null ? other.getStudyYear() : 2,
                    "photoUrl", other.getPhotoUrl() != null ? other.getPhotoUrl() : "",
                    "level", other.getLevel() != null ? other.getLevel() : "Beginner",
                    "averageRating", rating,
                    "reliabilityScore", gamificationService.calculateReliabilityScore(other.getId())
            ));
            match.put("matchScore", matchScore);
            match.put("theyCanTeachYou", theyCanTeachMe);
            match.put("youCanTeachThem", iCanTeachThem);
            match.put("allTeachableSkills", otherTeachable.stream().map(skill -> {
                Map<String, Object> teachableSkill = new HashMap<>();
                teachableSkill.put("skillId", skill.getSkillId());
                teachableSkill.put("skillName", skill.getSkillName());
                teachableSkill.put("proficiency", skill.getProficiency());
                teachableSkill.put("verified", Boolean.TRUE.equals(skill.getVerified()));
                return teachableSkill;
            }).collect(Collectors.toList()));
            match.put("hasVerifiedTutorTick", hasVerifiedTutorTick);
            match.put("availability", otherAvail);

            matches.add(match);
        }

        matches.sort((a, b) -> {
            Map<?, ?> aUser = (Map<?, ?>) a.get("user");
            Map<?, ?> bUser = (Map<?, ?>) b.get("user");
            boolean aSameCollege = currentCollegeDomain.equalsIgnoreCase((String) aUser.get("collegeDomain"));
            boolean bSameCollege = currentCollegeDomain.equalsIgnoreCase((String) bUser.get("collegeDomain"));
            if (aSameCollege != bSameCollege) return aSameCollege ? -1 : 1;
            return Integer.compare((Integer) b.get("matchScore"), (Integer) a.get("matchScore"));
        });
        return matches;
    }

    public List<Map<String, Object>> browseTutors(Long currentUserId, String skillFilter) {
        String currentCollegeDomain = userRepository.findById(currentUserId)
                .map(user -> extractEmailDomain(user.getEmail()))
                .orElse("");
        List<UserSkill> teachSkills = userSkillRepository.findBySkillType("CAN_TEACH");
        if (StringUtils.hasText(skillFilter)) {
            teachSkills = teachSkills.stream()
                    .filter(s -> s.getSkillName().toLowerCase().contains(skillFilter.toLowerCase().trim()))
                    .collect(Collectors.toList());
        }

        Map<Long, List<UserSkill>> byUser = teachSkills.stream().collect(Collectors.groupingBy(UserSkill::getUserId));
        List<Map<String, Object>> tutors = new ArrayList<>();

        for (Map.Entry<Long, List<UserSkill>> entry : byUser.entrySet()) {
            Long userId = entry.getKey();
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) continue;
            User user = userOpt.get();
            if (user.getStatus() != UserStatus.VERIFIED) continue;

            double rating = gamificationService.calculateAverageRating(userId);
            double reliability = gamificationService.calculateReliabilityScore(userId);
            boolean verified = entry.getValue().stream().anyMatch(s -> Boolean.TRUE.equals(s.getVerified()));

            Map<String, Object> map = new HashMap<>();
            map.put("user", Map.of(
                    "id", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail(),
                    "collegeDomain", extractEmailDomain(user.getEmail()),
                    "branch", user.getBranch() != null ? user.getBranch() : "CSE",
                    "studyYear", user.getStudyYear() != null ? user.getStudyYear() : 2,
                    "photoUrl", user.getPhotoUrl() != null ? user.getPhotoUrl() : "",
                    "level", user.getLevel() != null ? user.getLevel() : "Beginner",
                    "averageRating", rating,
                    "reliabilityScore", reliability
            ));
            map.put("skills", entry.getValue());
            map.put("isVerifiedTutor", verified);
            map.put("availability", availabilityRepository.findByUserId(userId));
            tutors.add(map);
        }

        tutors.sort((a, b) -> {
            Map<?, ?> aUser = (Map<?, ?>) a.get("user");
            Map<?, ?> bUser = (Map<?, ?>) b.get("user");
            boolean aSameCollege = currentCollegeDomain.equalsIgnoreCase((String) aUser.get("collegeDomain"));
            boolean bSameCollege = currentCollegeDomain.equalsIgnoreCase((String) bUser.get("collegeDomain"));
            if (aSameCollege != bSameCollege) return aSameCollege ? -1 : 1;
            return Double.compare((Double) bUser.get("averageRating"), (Double) aUser.get("averageRating"));
        });
        return tutors;
    }

    private String extractEmailDomain(String email) {
        int separator = email.lastIndexOf('@');
        return separator >= 0 ? email.substring(separator + 1).trim().toLowerCase() : "";
    }
}
