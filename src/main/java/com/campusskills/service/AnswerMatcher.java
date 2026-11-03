package com.campusskills.service;

import java.math.BigDecimal;
import java.math.MathContext;

final class AnswerMatcher {
    private static final MathContext FRACTION_CONTEXT = MathContext.DECIMAL128;

    private AnswerMatcher() {}

    static boolean matchesNumeric(String studentAnswer, String correctAnswer, BigDecimal tolerance) {
        try {
            BigDecimal studentValue = parseNumber(studentAnswer);
            BigDecimal correctValue = parseNumber(correctAnswer);
            return studentValue.subtract(correctValue).abs().compareTo(tolerance) <= 0;
        } catch (NumberFormatException | ArithmeticException e) {
            return false;
        }
    }

    static boolean matchesShortAnswer(String studentAnswer, String correctAnswer) {
        if (normalize(studentAnswer).equalsIgnoreCase(normalize(correctAnswer))) {
            return true;
        }

        return matchesNumeric(studentAnswer, correctAnswer, BigDecimal.ZERO);
    }

    private static String normalize(String answer) {
        return answer == null ? "" : answer.trim().replaceAll("\\s+", " ");
    }

    private static BigDecimal parseNumber(String answer) {
        String normalized = answer == null ? "" : answer.trim();
        int slashIndex = normalized.indexOf('/');
        if (slashIndex < 0) {
            return new BigDecimal(normalized);
        }
        if (slashIndex == 0 || slashIndex == normalized.length() - 1
                || normalized.indexOf('/', slashIndex + 1) >= 0) {
            throw new NumberFormatException("Invalid fraction");
        }

        BigDecimal numerator = new BigDecimal(normalized.substring(0, slashIndex).trim());
        BigDecimal denominator = new BigDecimal(normalized.substring(slashIndex + 1).trim());
        return numerator.divide(denominator, FRACTION_CONTEXT);
    }
}
