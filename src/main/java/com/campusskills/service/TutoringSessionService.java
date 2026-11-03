package com.campusskills.service;

import com.campusskills.dto.RatingRequest;
import com.campusskills.dto.SessionCreateRequest;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TutoringSessionService {

    private final TutoringSessionRepository tutoringSessionRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RatingRepository ratingRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final TextModerationService textModerationService;

    public TutoringSessionService(TutoringSessionRepository tutoringSessionRepository,
                                  UserRepository userRepository,
                                  SkillRepository skillRepository,
                                  RatingRepository ratingRepository,
                                  GamificationService gamificationService,
                                  NotificationService notificationService,
                                  TextModerationService textModerationService) {
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.ratingRepository = ratingRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public TutoringSession createSession(Long userId, SessionCreateRequest req) {
        Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new ApiException("Skill not found", HttpStatus.NOT_FOUND));

        User student = userRepository.findById(req.getStudentId())
                .orElseThrow(() -> new ApiException("Student not found", HttpStatus.NOT_FOUND));

        int creditsNeeded = (int) Math.ceil((req.getDurationMinutes() != null ? req.getDurationMinutes() : 60) / 60.0);
        if (student.getSkillCredits() < creditsNeeded) {
            throw new ApiException("Learner does not have enough SkillCredits (needed: " + creditsNeeded + ")", HttpStatus.BAD_REQUEST);
        }

        TutoringSession session = TutoringSession.builder()
                .swapRequestId(req.getSwapRequestId())
                .tutorId(req.getTutorId())
                .studentId(req.getStudentId())
                .skillId(skill.getId())
                .skillName(skill.getName())
                .scheduledTime(req.getScheduledTime() != null ? req.getScheduledTime() : LocalDateTime.now().plusDays(1))
                .durationMinutes(req.getDurationMinutes() != null ? req.getDurationMinutes() : 60)
                .mode(req.getMode() != null ? req.getMode() : "ONLINE")
                .meetingLink(req.getMeetingLink())
                .location(req.getLocation())
                .notes(req.getNotes())
                .status("SCHEDULED")
                .creditsCharged(creditsNeeded)
                .createdAt(LocalDateTime.now())
                .build();

        TutoringSession saved = tutoringSessionRepository.save(session);

        Long otherId = userId.equals(saved.getTutorId()) ? saved.getStudentId() : saved.getTutorId();
        notificationService.sendNotification(
                otherId,
                "New Tutoring Session Scheduled",
                "A tutoring session for " + skill.getName() + " has been scheduled.",
                "SESSION",
                saved.getId()
        );

        return saved;
    }

    public List<Map<String, Object>> getMySessions(Long userId) {
        List<TutoringSession> sessions = tutoringSessionRepository.findByTutorIdOrStudentIdOrderByScheduledTimeDesc(userId, userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (TutoringSession s : sessions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("tutorId", s.getTutorId());
            map.put("studentId", s.getStudentId());
            map.put("skillId", s.getSkillId());
            map.put("skillName", s.getSkillName());
            map.put("scheduledTime", s.getScheduledTime());
            map.put("durationMinutes", s.getDurationMinutes());
            map.put("mode", s.getMode());
            map.put("meetingLink", s.getMeetingLink());
            map.put("location", s.getLocation());
            map.put("status", s.getStatus());
            map.put("creditsCharged", s.getCreditsCharged());
            map.put("notes", s.getNotes());
            map.put("createdAt", s.getCreatedAt());
            map.put("isTutor", s.getTutorId().equals(userId));

            userRepository.findById(s.getTutorId()).ifPresent(t -> {
                map.put("tutorName", t.getFullName());
                map.put("tutorEmail", t.getEmail());
                map.put("tutorPhoto", t.getPhotoUrl());
            });

            userRepository.findById(s.getStudentId()).ifPresent(st -> {
                map.put("studentName", st.getFullName());
                map.put("studentEmail", st.getEmail());
                map.put("studentPhoto", st.getPhotoUrl());
            });

            // Check if current user already rated this session
            boolean alreadyRated = ratingRepository.existsBySessionIdAndRaterId(s.getId(), userId);
            map.put("alreadyRated", alreadyRated);

            result.add(map);
        }
        return result;
    }

    @Transactional
    public TutoringSession completeSession(Long userId, Long sessionId) {
        TutoringSession session = tutoringSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("Session not found", HttpStatus.NOT_FOUND));

        if (!session.getTutorId().equals(userId)) {
            throw new ApiException("Only the tutor can mark a session complete", HttpStatus.FORBIDDEN);
        }

        if ("COMPLETED".equalsIgnoreCase(session.getStatus())) {
            throw new ApiException("Session is already marked as completed", HttpStatus.BAD_REQUEST);
        }

        int duration = session.getDurationMinutes() != null ? session.getDurationMinutes() : 60;
        int credits = (int) Math.ceil(duration / 60.0);
        if (credits < 1) credits = 1;
        session.setCreditsCharged(credits);
        session.setStatus("COMPLETED");

        User student = userRepository.findById(session.getStudentId())
                .orElseThrow(() -> new ApiException("Student not found", HttpStatus.NOT_FOUND));
        User tutor = userRepository.findById(session.getTutorId())
                .orElseThrow(() -> new ApiException("Tutor not found", HttpStatus.NOT_FOUND));

        // SkillCredits exchange
        student.setSkillCredits(Math.max(0, (student.getSkillCredits() != null ? student.getSkillCredits() : 0) - credits));
        tutor.setSkillCredits((tutor.getSkillCredits() != null ? tutor.getSkillCredits() : 0) + credits);

        userRepository.save(student);
        userRepository.save(tutor);

        // XP rules: teach a session +30, learn +15
        gamificationService.awardXp(tutor.getId(), 30, "Taught " + session.getSkillName() + " (" + duration + " min)");
        gamificationService.awardXp(student.getId(), 15, "Learned " + session.getSkillName() + " (" + duration + " min)");

        // Notify student to rate
        notificationService.sendNotification(
                student.getId(),
                "Session Completed!",
                tutor.getFullName() + " marked your " + session.getSkillName() + " session complete. You paid " + credits + " credit(s). Don't forget to leave a review!",
                "RATING",
                session.getId()
        );

        return tutoringSessionRepository.save(session);
    }

    @Transactional
    public Rating rateSession(Long userId, Long sessionId, RatingRequest req) {
        textModerationService.check(userId, "TUTORING_REVIEW", req.getComment());
        TutoringSession session = tutoringSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException("Session not found", HttpStatus.NOT_FOUND));

        if (!"COMPLETED".equalsIgnoreCase(session.getStatus())) {
            throw new ApiException("You can only rate a completed session", HttpStatus.BAD_REQUEST);
        }

        if (!session.getTutorId().equals(userId) && !session.getStudentId().equals(userId)) {
            throw new ApiException("You were not a participant in this session", HttpStatus.FORBIDDEN);
        }

        if (ratingRepository.existsBySessionIdAndRaterId(sessionId, userId)) {
            throw new ApiException("You have already rated this session", HttpStatus.CONFLICT);
        }

        Long ratedUserId = userId.equals(session.getTutorId()) ? session.getStudentId() : session.getTutorId();

        Rating rating = Rating.builder()
                .sessionId(sessionId)
                .raterId(userId)
                .ratedUserId(ratedUserId)
                .rating(req.getRating())
                .comment(req.getComment())
                .context("SESSION")
                .createdAt(LocalDateTime.now())
                .build();

        Rating saved = ratingRepository.save(rating);

        User rater = userRepository.findById(userId).orElse(null);
        String raterName = rater != null ? rater.getFullName() : "A peer";

        notificationService.sendNotification(
                ratedUserId,
                "New Rating Received",
                raterName + " gave you a " + req.getRating() + "★ rating for the " + session.getSkillName() + " session.",
                "RATING",
                saved.getId()
        );

        return saved;
    }
}
