package com.campusskills.service;

import com.campusskills.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class TextModerationService {
    private static final List<ModerationRule> RULES = List.of(
            rule("ABUSIVE_LANGUAGE", "fuck", "fucking", "fucked", "fucker", "fuckers", "shit", "shitty",
                    "shitting", "bitch", "bitches", "asshole", "assholes", "motherfucker", "motherfuckers",
                    "bastard", "bastards", "cunt", "cunts"),
            rule("SEXUAL_OR_NUDITY", "porn", "pornography", "nude", "nudes", "naked", "sex", "sexual",
                    "blowjob", "handjob", "hentai", "sext", "sexting", "erotic", "penis", "vagina",
                    "genital", "genitals")
    );

    private final ModerationReportService moderationReportService;

    public TextModerationService(ModerationReportService moderationReportService) {
        this.moderationReportService = moderationReportService;
    }

    public void check(Long userId, String surface, String... submittedText) {
        checkText(userId, surface, submittedText);
    }

    public void checkRegistration(String email, String... submittedText) {
        checkText(0L, "REGISTRATION (" + email + ")", submittedText);
    }

    private void checkText(Long userId, String surface, String... submittedText) {
        String text = String.join("\n", submittedText == null ? new String[0] : submittedText);
        if (!StringUtils.hasText(text)) {
            return;
        }

        String normalized = normalize(text);
        for (ModerationRule rule : RULES) {
            if (rule.pattern().matcher(normalized).find()) {
                moderationReportService.recordBlockedSubmission(userId, surface, rule.category(), text);
                throw new ApiException(
                        "This submission was blocked by the content policy. An administrator has been notified for review.",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private String normalize(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
        normalized = normalized.replace('0', 'o').replace('1', 'i').replace('3', 'e')
                .replace('4', 'a').replace('5', 's').replace('7', 't').replace('@', 'a')
                .replace('$', 's');
        return normalized.replaceAll("[^\\p{L}\\p{N}]+", " ");
    }

    private static ModerationRule rule(String category, String... words) {
        String expression = String.join("|", java.util.Arrays.stream(words)
                .map(word -> word.chars()
                        .mapToObj(character -> Pattern.quote(String.valueOf((char) character)))
                        .collect(java.util.stream.Collectors.joining("[\\s\\p{Punct}]*")))
                .toList());
        return new ModerationRule(category, Pattern.compile(
                "(?i)(?<![\\p{L}\\p{N}])(?:" + expression + ")(?![\\p{L}\\p{N}])"));
    }

    private record ModerationRule(String category, Pattern pattern) {
    }
}
