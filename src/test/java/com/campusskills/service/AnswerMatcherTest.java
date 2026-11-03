package com.campusskills.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AnswerMatcherTest {
    @Test
    void acceptsEquivalentFractionAnswers() {
        assertTrue(AnswerMatcher.matchesNumeric("1/2", "1/2", BigDecimal.ZERO));
        assertTrue(AnswerMatcher.matchesNumeric("2/4", "1/2", BigDecimal.ZERO));
        assertTrue(AnswerMatcher.matchesShortAnswer("1 / 2", "1/2"));
        assertTrue(AnswerMatcher.matchesShortAnswer("0.5", "1/2"));
    }

    @Test
    void rejectsInvalidFractionsAndZeroDenominators() {
        assertFalse(AnswerMatcher.matchesNumeric("1/0", "1/2", BigDecimal.ZERO));
        assertFalse(AnswerMatcher.matchesNumeric("one half", "1/2", BigDecimal.ZERO));
        assertFalse(AnswerMatcher.matchesNumeric("1/3", "1/2", BigDecimal.ZERO));
    }

    @Test
    void preservesNumericToleranceForDecimalAnswers() {
        assertTrue(AnswerMatcher.matchesNumeric("8.005", "8", new BigDecimal("0.01")));
        assertFalse(AnswerMatcher.matchesNumeric("8.02", "8", new BigDecimal("0.01")));
    }
}
