package com.campusskills.service;

import com.campusskills.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TextModerationServiceTest {
    @Test
    void blocksAbusiveTextAndReportsEvidence() {
        ModerationReportService reports = mock(ModerationReportService.class);
        TextModerationService moderation = new TextModerationService(reports);

        assertThrows(ApiException.class, () -> moderation.check(12L, "GROUP_CHAT", "you are a f.u.c.k"));

        verify(reports).recordBlockedSubmission(12L, "GROUP_CHAT", "ABUSIVE_LANGUAGE", "you are a f.u.c.k");
    }

    @Test
    void blocksSexualTextAndReportsEvidence() {
        ModerationReportService reports = mock(ModerationReportService.class);
        TextModerationService moderation = new TextModerationService(reports);

        assertThrows(ApiException.class, () -> moderation.check(12L, "DOUBT", "sharing nudes"));

        verify(reports).recordBlockedSubmission(12L, "DOUBT", "SEXUAL_OR_NUDITY", "sharing nudes");
    }

    @Test
    void allowsOrdinaryTechnicalLanguage() {
        ModerationReportService reports = mock(ModerationReportService.class);
        TextModerationService moderation = new TextModerationService(reports);

        moderation.check(12L, "DOUBT", "How do I improve this Java class?");

        verifyNoInteractions(reports);
    }

    @Test
    void reportsBlockedRegistrationWithEmailContext() {
        ModerationReportService reports = mock(ModerationReportService.class);
        TextModerationService moderation = new TextModerationService(reports);

        assertThrows(ApiException.class, () -> moderation.checkRegistration(
                "student@example.edu", "nudes"));

        verify(reports).recordBlockedSubmission(
                0L,
                "REGISTRATION (student@example.edu)",
                "SEXUAL_OR_NUDITY",
                "nudes");
    }
}
