package com.campusskills.service;

import com.campusskills.entity.Report;
import com.campusskills.entity.Role;
import com.campusskills.repository.ReportRepository;
import com.campusskills.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ModerationReportService {
    private static final Logger log = LoggerFactory.getLogger(ModerationReportService.class);

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ModerationReportService(ReportRepository reportRepository,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordBlockedSubmission(Long userId, String surface, String category, String evidence) {
        String reportReason = ("AUTOMATED CONTENT MODERATION | Area: " + surface
                + " | Category: " + category + " | Evidence: " + evidence);
        if (reportReason.length() > 20000) {
            reportReason = reportReason.substring(0, 20000);
        }

        Report savedReport = reportRepository.save(Report.builder()
                .reporterId(userId)
                .reportedType("USER")
                .reportedId(userId)
                .reason(reportReason)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build());

        userRepository.findByRole(Role.SUPER_ADMIN).forEach(admin -> {
            try {
                notificationService.sendNotification(
                        admin.getId(),
                        "Content moderation report",
                        "A user submission was blocked. Review report #" + savedReport.getId() + " in the Admin Console.",
                        "MODERATION_REPORT",
                        savedReport.getId());
            } catch (RuntimeException e) {
                log.error("Could not notify administrator {} about moderation report {}", admin.getId(), savedReport.getId(), e);
            }
        });
    }
}
