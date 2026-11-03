package com.campusskills.service;

import com.campusskills.dto.SwapRequestAction;
import com.campusskills.dto.SwapRequestCreate;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SwapService {

    private final SwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final TutoringSessionRepository tutoringSessionRepository;
    private final NotificationService notificationService;
    private final TextModerationService textModerationService;

    public SwapService(SwapRequestRepository swapRequestRepository,
                       UserRepository userRepository,
                       SkillRepository skillRepository,
    UserSkillRepository userSkillRepository,
    TutoringSessionRepository tutoringSessionRepository,
    NotificationService notificationService,
    TextModerationService textModerationService) {
        this.swapRequestRepository = swapRequestRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.notificationService = notificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public SwapRequest createSwapRequest(Long senderId, SwapRequestCreate req) {
        textModerationService.check(senderId, "SKILL_SWAP_REQUEST", req.getMessage());
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (sender.getSkillCredits() == null || sender.getSkillCredits() < 1) {
            throw new ApiException("You need at least 1 SkillCredit to send a swap request.", HttpStatus.BAD_REQUEST);
        }

        if (senderId.equals(req.getReceiverId())) {
            throw new ApiException("You cannot send a swap request to yourself.", HttpStatus.BAD_REQUEST);
        }

        User receiver = userRepository.findById(req.getReceiverId())
                .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));

        Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new ApiException("Skill not found", HttpStatus.NOT_FOUND));

        boolean offeredByReceiver = userSkillRepository.findByUserId(receiver.getId()).stream()
                .anyMatch(userSkill -> Objects.equals(userSkill.getSkillId(), skill.getId())
                        && "CAN_TEACH".equalsIgnoreCase(userSkill.getSkillType()));
        if (!offeredByReceiver) {
            throw new ApiException("The selected skill is not offered by this tutor.", HttpStatus.BAD_REQUEST);
        }

        SwapRequest swapRequest = SwapRequest.builder()
                .senderId(senderId)
                .receiverId(receiver.getId())
                .skillId(skill.getId())
                .skillName(skill.getName())
                .message(req.getMessage())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SwapRequest saved = swapRequestRepository.save(swapRequest);

        // Notify receiver
        notificationService.sendNotification(
                receiver.getId(),
                "New Skill Swap Request",
                sender.getFullName() + " requested a tutoring session with you for " + skill.getName() + ".",
                "REQUEST",
                saved.getId()
        );

        return saved;
    }

    public List<Map<String, Object>> getMySwapRequests(Long userId) {
        List<SwapRequest> list = swapRequestRepository.findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SwapRequest sr : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sr.getId());
            map.put("senderId", sr.getSenderId());
            map.put("receiverId", sr.getReceiverId());
            map.put("skillId", sr.getSkillId());
            map.put("skillName", sr.getSkillName());
            map.put("message", sr.getMessage());
            map.put("status", sr.getStatus());
            map.put("createdAt", sr.getCreatedAt());
            map.put("isSender", sr.getSenderId().equals(userId));

            userRepository.findById(sr.getSenderId()).ifPresent(s -> {
                map.put("senderName", s.getFullName());
                map.put("senderPhoto", s.getPhotoUrl());
                map.put("senderEmail", s.getEmail());
            });

            userRepository.findById(sr.getReceiverId()).ifPresent(r -> {
                map.put("receiverName", r.getFullName());
                map.put("receiverPhoto", r.getPhotoUrl());
                map.put("receiverEmail", r.getEmail());
            });

            result.add(map);
        }
        return result;
    }

    @Transactional
    public SwapRequest actionSwapRequest(Long userId, Long requestId, SwapRequestAction actionReq) {
        SwapRequest sr = swapRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("Swap request not found", HttpStatus.NOT_FOUND));

        String action = actionReq.getAction().toUpperCase();

        if (action.equals("CANCEL")) {
            if (!sr.getSenderId().equals(userId)) {
                throw new ApiException("Only the sender can cancel this request", HttpStatus.FORBIDDEN);
            }
            sr.setStatus("CANCELLED");
        } else if (action.equals("ACCEPT")) {
            if (!sr.getReceiverId().equals(userId)) {
                throw new ApiException("Only the recipient can accept this request", HttpStatus.FORBIDDEN);
            }
            sr.setStatus("ACCEPTED");

            // Automatically create a scheduled session between receiver (tutor) and sender (student)
            TutoringSession session = TutoringSession.builder()
                    .swapRequestId(sr.getId())
                    .tutorId(sr.getReceiverId())
                    .studentId(sr.getSenderId())
                    .skillId(sr.getSkillId())
                    .skillName(sr.getSkillName())
                    .scheduledTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(0))
                    .durationMinutes(60)
                    .mode("ONLINE")
                    .meetingLink("https://meet.google.com/campus-" + UUID.randomUUID().toString().substring(0, 8))
                    .status("SCHEDULED")
                    .creditsCharged(1)
                    .notes(sr.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();
            tutoringSessionRepository.save(session);

            notificationService.sendNotification(
                    sr.getSenderId(),
                    "Swap Request Accepted!",
                    "Your swap request for " + sr.getSkillName() + " was accepted! A 1-hour session has been scheduled.",
                    "SESSION",
                    session.getId()
            );
        } else if (action.equals("REJECT")) {
            if (!sr.getReceiverId().equals(userId)) {
                throw new ApiException("Only the recipient can reject this request", HttpStatus.FORBIDDEN);
            }
            sr.setStatus("REJECTED");
            notificationService.sendNotification(
                    sr.getSenderId(),
                    "Swap Request Declined",
                    "Your swap request for " + sr.getSkillName() + " was declined.",
                    "REQUEST",
                    sr.getId()
            );
        } else {
            throw new ApiException("Invalid action. Must be ACCEPT, REJECT, or CANCEL", HttpStatus.BAD_REQUEST);
        }

        return swapRequestRepository.save(sr);
    }
}
