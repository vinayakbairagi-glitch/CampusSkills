package com.campusskills.service;

import com.campusskills.entity.Report;
import com.campusskills.entity.Role;
import com.campusskills.entity.User;
import com.campusskills.repository.ReportRepository;
import com.campusskills.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ModerationReportServiceTest {
    @Test
    void recordsEvidenceAndNotifiesSuperAdmins() {
        ReportRepository reports = mock(ReportRepository.class);
        UserRepository users = mock(UserRepository.class);
        NotificationService notifications = mock(NotificationService.class);
        User admin = User.builder().id(1L).role(Role.SUPER_ADMIN).build();
        when(users.findByRole(Role.SUPER_ADMIN)).thenReturn(List.of(admin));
        when(reports.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            report.setId(42L);
            return report;
        });
        ModerationReportService service = new ModerationReportService(reports, users, notifications);

        service.recordBlockedSubmission(12L, "GROUP_CHAT", "ABUSIVE_LANGUAGE", "blocked words here");

        verify(reports).save(argThat(report ->
                report.getReporterId().equals(12L)
                        && report.getReportedType().equals("USER")
                        && report.getReportedId().equals(12L)
                        && report.getStatus().equals("PENDING")
                        && report.getReason().contains("GROUP_CHAT")
                        && report.getReason().contains("blocked words here")));
        verify(notifications).sendNotification(eq(1L), eq("Content moderation report"),
                contains("42"), eq("MODERATION_REPORT"), eq(42L));
    }
}
