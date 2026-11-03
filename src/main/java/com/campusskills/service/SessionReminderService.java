package com.campusskills.service;

import com.campusskills.entity.GroupSession;
import com.campusskills.entity.TutoringSession;
import com.campusskills.repository.GroupMemberRepository;
import com.campusskills.repository.GroupSessionRepository;
import com.campusskills.repository.NotificationRepository;
import com.campusskills.repository.TutoringSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SessionReminderService {
    private static final Logger log = LoggerFactory.getLogger(SessionReminderService.class);
    private static final String TUTORING_REMINDER = "TUTORING_SESSION_REMINDER";
    private static final String GROUP_REMINDER = "GROUP_SESSION_REMINDER";
    private static final DateTimeFormatter SESSION_TIME = DateTimeFormatter.ofPattern("MMM d 'at' h:mm a");

    private final TutoringSessionRepository tutoringSessionRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public SessionReminderService(TutoringSessionRepository tutoringSessionRepository,
                                  GroupSessionRepository groupSessionRepository,
                                  GroupMemberRepository groupMemberRepository,
                                  NotificationRepository notificationRepository,
                                  NotificationService notificationService) {
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${campusskills.reminders.schedule:0 * * * * *}")
    @Transactional
    public void sendUpcomingSessionReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindowEnd = now.plusMinutes(15);

        tutoringSessionRepository.findByStatusAndScheduledTimeBetween("SCHEDULED", now, reminderWindowEnd)
                .forEach(session -> sendTutoringReminder(session));
        groupSessionRepository.findByScheduledTimeBetween(now, reminderWindowEnd)
                .forEach(session -> sendGroupReminder(session));
    }

    private void sendTutoringReminder(TutoringSession session) {
        String title = "Tutoring session starts soon";
        String message = session.getSkillName() + " session starts " + formatTime(session.getScheduledTime()) + ".";
        sendOnce(session.getTutorId(), title, message, TUTORING_REMINDER, session.getId());
        sendOnce(session.getStudentId(), title, message, TUTORING_REMINDER, session.getId());
    }

    private void sendGroupReminder(GroupSession session) {
        String title = "Study group session starts soon";
        String message = session.getTitle() + " starts " + formatTime(session.getScheduledTime()) + ".";
        groupMemberRepository.findByGroupIdAndStatus(session.getGroupId(), "ACTIVE")
                .forEach(member -> sendOnce(member.getUserId(), title, message, GROUP_REMINDER, session.getId()));
    }

    private void sendOnce(Long userId, String title, String message, String type, Long sessionId) {
        if (notificationRepository.existsByUserIdAndNotificationTypeAndReferenceId(userId, type, sessionId)) {
            return;
        }
        try {
            notificationService.sendNotification(userId, title, message, type, sessionId);
        } catch (RuntimeException e) {
            log.error("Failed to send {} reminder for session {} to user {}", type, sessionId, userId, e);
        }
    }

    private String formatTime(LocalDateTime scheduledTime) {
        return scheduledTime.format(SESSION_TIME);
    }
}
