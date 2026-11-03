package com.campusskills.service;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DoubtService {

    private final DoubtRepository doubtRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final TextModerationService textModerationService;

    public DoubtService(DoubtRepository doubtRepository,
                        DoubtAnswerRepository doubtAnswerRepository,
                        AnswerUpvoteRepository answerUpvoteRepository,
                        ReportRepository reportRepository,
                        UserRepository userRepository,
                        GamificationService gamificationService,
                        NotificationService notificationService,
                        TextModerationService textModerationService) {
        this.doubtRepository = doubtRepository;
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.answerUpvoteRepository = answerUpvoteRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public Doubt createDoubt(Long userId, DoubtCreateRequest req) {
        textModerationService.check(userId, "DOUBT", req.getSubject(), req.getTopic(), req.getTitle(), req.getDescription());
        Doubt doubt = Doubt.builder()
                .askerId(userId)
                .subject(req.getSubject().trim())
                .topic(req.getTopic().trim())
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .attachmentUrl(req.getAttachmentUrl())
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .build();
        return doubtRepository.save(doubt);
    }

    public List<Map<String, Object>> searchDoubts(String query, String subject, String status) {
        List<Doubt> doubts = doubtRepository.searchDoubts(
                (query != null && !query.isBlank()) ? query.trim() : null,
                (subject != null && !subject.isBlank()) ? subject.trim() : null,
                (status != null && !status.isBlank()) ? status.trim() : null
        );

        List<Map<String, Object>> result = new ArrayList<>();
        for (Doubt d : doubts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("askerId", d.getAskerId());
            map.put("subject", d.getSubject());
            map.put("topic", d.getTopic());
            map.put("title", d.getTitle());
            map.put("description", d.getDescription());
            map.put("attachmentUrl", d.getAttachmentUrl());
            map.put("status", d.getStatus());
            map.put("bestAnswerId", d.getBestAnswerId());
            map.put("createdAt", d.getCreatedAt());

            userRepository.findById(d.getAskerId()).ifPresent(u -> {
                map.put("askerName", u.getFullName());
                map.put("askerPhoto", u.getPhotoUrl());
                map.put("askerBranch", u.getBranch());
                map.put("askerYear", u.getStudyYear());
            });

            long answersCount = doubtAnswerRepository.findByDoubtIdOrderByUpvoteCountDescCreatedAtAsc(d.getId()).size();
            map.put("answersCount", answersCount);

            result.add(map);
        }
        return result;
    }

    public Map<String, Object> getDoubtDetail(Long doubtId, Long currentUserId) {
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new ApiException("Doubt not found", HttpStatus.NOT_FOUND));

        Map<String, Object> map = new HashMap<>();
        map.put("id", doubt.getId());
        map.put("askerId", doubt.getAskerId());
        map.put("subject", doubt.getSubject());
        map.put("topic", doubt.getTopic());
        map.put("title", doubt.getTitle());
        map.put("description", doubt.getDescription());
        map.put("attachmentUrl", doubt.getAttachmentUrl());
        map.put("status", doubt.getStatus());
        map.put("bestAnswerId", doubt.getBestAnswerId());
        map.put("createdAt", doubt.getCreatedAt());
        map.put("isAsker", doubt.getAskerId().equals(currentUserId));

        userRepository.findById(doubt.getAskerId()).ifPresent(u -> {
            map.put("askerName", u.getFullName());
            map.put("askerPhoto", u.getPhotoUrl());
            map.put("askerBranch", u.getBranch());
            map.put("askerYear", u.getStudyYear());
        });

        // Answers
        List<DoubtAnswer> answers = doubtAnswerRepository.findByDoubtIdOrderByUpvoteCountDescCreatedAtAsc(doubtId);
        List<Map<String, Object>> answerList = new ArrayList<>();
        for (DoubtAnswer a : answers) {
            Map<String, Object> amap = new HashMap<>();
            amap.put("id", a.getId());
            amap.put("doubtId", a.getDoubtId());
            amap.put("authorId", a.getAuthorId());
            amap.put("authorName", a.getAuthorName());
            amap.put("authorYear", a.getAuthorYear());
            amap.put("content", a.getContent());
            amap.put("attachmentUrl", a.getAttachmentUrl());
            amap.put("isBest", a.getIsBest());
            amap.put("isVerified", a.getIsVerified());
            amap.put("upvoteCount", a.getUpvoteCount());
            amap.put("createdAt", a.getCreatedAt());
            amap.put("hasUpvoted", answerUpvoteRepository.existsByAnswerIdAndUserId(a.getId(), currentUserId));

            userRepository.findById(a.getAuthorId()).ifPresent(au -> {
                amap.put("authorPhoto", au.getPhotoUrl());
                amap.put("authorRole", au.getRole().name());
                amap.put("authorBranch", au.getBranch());
            });

            answerList.add(amap);
        }
        map.put("answers", answerList);

        return map;
    }

    @Transactional
    public DoubtAnswer addAnswer(Long doubtId, Long userId, AnswerCreateRequest req) {
        textModerationService.check(userId, "DOUBT_ANSWER", req.getContent());
        Doubt doubt = doubtRepository.findById(doubtId)
                .orElseThrow(() -> new ApiException("Doubt not found", HttpStatus.NOT_FOUND));

        if (doubt.getAskerId().equals(userId)) {
            throw new ApiException("You cannot answer your own doubt", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        DoubtAnswer answer = DoubtAnswer.builder()
                .doubtId(doubtId)
                .authorId(userId)
                .authorName(user.getFullName())
                .authorYear(user.getStudyYear())
                .content(req.getContent())
                .attachmentUrl(req.getAttachmentUrl())
                .isBest(false)
                .isVerified(false)
                .upvoteCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        DoubtAnswer saved = doubtAnswerRepository.save(answer);

        // XP rules: answer a doubt +5
        gamificationService.awardXp(userId, 5, "Answered doubt: " + doubt.getTitle());

        // Notify asker
        notificationService.sendNotification(
                doubt.getAskerId(),
                "New Answer on Your Doubt",
                user.getFullName() + " answered your question: " + doubt.getTitle(),
                "ANSWER",
                doubt.getId()
        );

        return saved;
    }

    @Transactional
    public Doubt markBestAnswer(Long answerId, Long userId) {
        DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException("Answer not found", HttpStatus.NOT_FOUND));

        Doubt doubt = doubtRepository.findById(answer.getDoubtId())
                .orElseThrow(() -> new ApiException("Doubt not found", HttpStatus.NOT_FOUND));

        if (!doubt.getAskerId().equals(userId)) {
            throw new ApiException("Only the person who posted the doubt can select the best answer", HttpStatus.FORBIDDEN);
        }

        // Unmark previous best answers for this doubt
        List<DoubtAnswer> allAnswers = doubtAnswerRepository.findByDoubtIdOrderByUpvoteCountDescCreatedAtAsc(doubt.getId());
        for (DoubtAnswer a : allAnswers) {
            if (Boolean.TRUE.equals(a.getIsBest())) {
                a.setIsBest(false);
                doubtAnswerRepository.save(a);
            }
        }

        answer.setIsBest(true);
        doubtAnswerRepository.save(answer);

        doubt.setBestAnswerId(answer.getId());
        doubt.setStatus("SOLVED");
        Doubt updatedDoubt = doubtRepository.save(doubt);

        // The best-answer author gets +1 credit and +25 XP
        userRepository.findById(answer.getAuthorId()).ifPresent(author -> {
            author.setSkillCredits((author.getSkillCredits() != null ? author.getSkillCredits() : 0) + 1);
            userRepository.save(author);
        });
        gamificationService.awardXp(answer.getAuthorId(), 25, "Best answer for doubt: " + doubt.getTitle());

        notificationService.sendNotification(
                answer.getAuthorId(),
                "Best Answer Accepted!",
                "Your answer was chosen as the BEST answer! You received +1 SkillCredit and +25 XP.",
                "BADGE",
                doubt.getId()
        );

        return updatedDoubt;
    }

    @Transactional
    public Map<String, Object> toggleUpvote(Long answerId, Long userId) {
        DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException("Answer not found", HttpStatus.NOT_FOUND));

        if (answer.getAuthorId().equals(userId)) {
            throw new ApiException("You cannot upvote your own answer", HttpStatus.BAD_REQUEST);
        }

        Optional<AnswerUpvote> upvote = answerUpvoteRepository.findByAnswerIdAndUserId(answerId, userId);
        boolean upvotedNow;
        if (upvote.isPresent()) {
            answerUpvoteRepository.delete(upvote.get());
            answer.setUpvoteCount(Math.max(0, (answer.getUpvoteCount() != null ? answer.getUpvoteCount() : 1) - 1));
            upvotedNow = false;
        } else {
            answerUpvoteRepository.save(AnswerUpvote.builder().answerId(answerId).userId(userId).build());
            answer.setUpvoteCount((answer.getUpvoteCount() != null ? answer.getUpvoteCount() : 0) + 1);
            upvotedNow = true;
        }

        doubtAnswerRepository.save(answer);
        return Map.of("upvoted", upvotedNow, "upvoteCount", answer.getUpvoteCount());
    }

    @Transactional
    public DoubtAnswer verifyAnswer(Long answerId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        // Seniors (year 3+) and faculty/TPO can verify
        boolean isEligible = (user.getStudyYear() != null && user.getStudyYear() >= 3) ||
                             user.getRole() == Role.TPO ||
                             user.getRole() == Role.SUPER_ADMIN ||
                             user.getRole() == Role.TUTOR;

        if (!isEligible) {
            throw new ApiException("Only seniors (Year 3+) and faculty/mentors can verify answers", HttpStatus.FORBIDDEN);
        }

        DoubtAnswer answer = doubtAnswerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException("Answer not found", HttpStatus.NOT_FOUND));

        answer.setIsVerified(true);
        answer.setVerifiedBy(userId);
        DoubtAnswer saved = doubtAnswerRepository.save(answer);

        notificationService.sendNotification(
                answer.getAuthorId(),
                "Answer Verified",
                user.getFullName() + " verified your answer!",
                "ANSWER",
                answer.getDoubtId()
        );

        return saved;
    }

    @Transactional
    public Report createReport(Long userId, ReportCreateRequest req) {
        Report report = Report.builder()
                .reporterId(userId)
                .reportedType(req.getReportedType().toUpperCase().trim())
                .reportedId(req.getReportedId())
                .reason(req.getReason().trim())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        return reportRepository.save(report);
    }
}
