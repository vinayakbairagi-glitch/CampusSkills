package com.campusskills.service;

import com.campusskills.entity.GroupSession;
import com.campusskills.entity.GroupMember;
import com.campusskills.entity.TutoringSession;
import com.campusskills.repository.GroupMemberRepository;
import com.campusskills.repository.GroupSessionRepository;
import com.campusskills.repository.NotificationRepository;
import com.campusskills.repository.TutoringSessionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SessionReminderServiceTest {
    @Test
    void sendsTutoringReminderToBothParticipantsOnce() {
        TutoringSessionRepository tutoringSessions = mock(TutoringSessionRepository.class);
        GroupSessionRepository groupSessions = mock(GroupSessionRepository.class);
        GroupMemberRepository groupMembers = mock(GroupMemberRepository.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        TutoringSession session = TutoringSession.builder()
                .id(25L)
                .tutorId(4L)
                .studentId(8L)
                .skillName("Java")
                .scheduledTime(LocalDateTime.now().plusMinutes(10))
                .status("SCHEDULED")
                .build();
        when(tutoringSessions.findByStatusAndScheduledTimeBetween(eq("SCHEDULED"), any(), any()))
                .thenReturn(List.of(session));
        when(groupSessions.findByScheduledTimeBetween(any(), any())).thenReturn(List.of());

        SessionReminderService service = new SessionReminderService(
                tutoringSessions, groupSessions, groupMembers, notifications, notificationService);

        service.sendUpcomingSessionReminders();

        verify(notificationService).sendNotification(eq(4L), eq("Tutoring session starts soon"),
                contains("Java"), eq("TUTORING_SESSION_REMINDER"), eq(25L));
        verify(notificationService).sendNotification(eq(8L), eq("Tutoring session starts soon"),
                contains("Java"), eq("TUTORING_SESSION_REMINDER"), eq(25L));
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doesNotRepeatReminderAlreadyStoredForSession() {
        TutoringSessionRepository tutoringSessions = mock(TutoringSessionRepository.class);
        GroupSessionRepository groupSessions = mock(GroupSessionRepository.class);
        GroupMemberRepository groupMembers = mock(GroupMemberRepository.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        TutoringSession session = TutoringSession.builder()
                .id(25L)
                .tutorId(4L)
                .studentId(8L)
                .skillName("Java")
                .scheduledTime(LocalDateTime.now().plusMinutes(10))
                .status("SCHEDULED")
                .build();
        when(tutoringSessions.findByStatusAndScheduledTimeBetween(eq("SCHEDULED"), any(), any()))
                .thenReturn(List.of(session));
        when(groupSessions.findByScheduledTimeBetween(any(), any())).thenReturn(List.of());
        when(notifications.existsByUserIdAndNotificationTypeAndReferenceId(
                anyLong(), eq("TUTORING_SESSION_REMINDER"), eq(25L))).thenReturn(true);

        SessionReminderService service = new SessionReminderService(
                tutoringSessions, groupSessions, groupMembers, notifications, notificationService);

        service.sendUpcomingSessionReminders();

        verifyNoInteractions(notificationService);
    }

    @Test
    void sendsGroupReminderToActiveMembers() {
        TutoringSessionRepository tutoringSessions = mock(TutoringSessionRepository.class);
        GroupSessionRepository groupSessions = mock(GroupSessionRepository.class);
        GroupMemberRepository groupMembers = mock(GroupMemberRepository.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        GroupSession session = GroupSession.builder()
                .id(31L)
                .groupId(12L)
                .title("DSA Study Circle")
                .scheduledTime(LocalDateTime.now().plusMinutes(10))
                .build();
        when(tutoringSessions.findByStatusAndScheduledTimeBetween(eq("SCHEDULED"), any(), any()))
                .thenReturn(List.of());
        when(groupSessions.findByScheduledTimeBetween(any(), any())).thenReturn(List.of(session));
        when(groupMembers.findByGroupIdAndStatus(12L, "ACTIVE")).thenReturn(List.of(
                GroupMember.builder().userId(4L).build(),
                GroupMember.builder().userId(8L).build()));

        SessionReminderService service = new SessionReminderService(
                tutoringSessions, groupSessions, groupMembers, notifications, notificationService);

        service.sendUpcomingSessionReminders();

        verify(notificationService).sendNotification(eq(4L), eq("Study group session starts soon"),
                contains("DSA Study Circle"), eq("GROUP_SESSION_REMINDER"), eq(31L));
        verify(notificationService).sendNotification(eq(8L), eq("Study group session starts soon"),
                contains("DSA Study Circle"), eq("GROUP_SESSION_REMINDER"), eq(31L));
        verifyNoMoreInteractions(notificationService);
    }
}
