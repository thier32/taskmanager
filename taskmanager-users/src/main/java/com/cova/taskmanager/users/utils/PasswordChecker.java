package com.cova.taskmanager.users.utils;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordChecker {

    // Regex Patterns
    private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    @Value("${app.security.password.length-regex}")
    private String lengthRegex;

    @Value("${app.security.password.uppercase-regex}")
    private String uppercaseRegex;

    @Value("${app.security.password.lowercase-regex}")
    private String lowercaseRegex;

    @Value("${app.security.password.digit-regex}")
    private String digitRegex;

    @Value("${app.security.password.special-char-regex}")
    private String specialCharRegex;

    public enum Strength {
        VERY_WEAK,
        WEAK,
        MEDIUM,
        STRONG,
        VERY_STRONG
    }

    private Pattern lengthPattern;
    private Pattern uppercasePattern;
    private Pattern lowercasePattern;
    private Pattern digitPattern;
    private Pattern specialCharPattern;

    @PostConstruct
    public void init() {
        this.lengthPattern = Pattern.compile(lengthRegex);
        this.uppercasePattern = Pattern.compile(uppercaseRegex);
        this.lowercasePattern = Pattern.compile(lowercaseRegex);
        this.digitPattern = Pattern.compile(digitRegex);
        this.specialCharPattern = Pattern.compile(specialCharRegex);
    }

    public record Result(Strength strength, int score, List<String> missingRequirements, boolean isValid) {}

    /**
     * Evaluates password strength and missing criteria.
     */
    public static Result evaluate(String password) {
        if (password == null || password.isEmpty()) {
            return new Result(Strength.VERY_WEAK, 0, List.of("Password cannot be empty"), false);
        }

        List<String> missing = new ArrayList<>();
        int score = 0;

        if (LENGTH_PATTERN.matcher(password).matches()) {
            score++;
        } else {
            missing.add("Must be at least 8 characters long");
        }

        if (UPPERCASE_PATTERN.matcher(password).matches()) {
            score++;
        } else {
            missing.add("Must contain at least one uppercase letter");
        }

        if (LOWERCASE_PATTERN.matcher(password).matches()) {
            score++;
        } else {
            missing.add("Must contain at least one lowercase letter");
        }

        if (DIGIT_PATTERN.matcher(password).matches()) {
            score++;
        } else {
            missing.add("Must contain at least one digit");
        }

        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            score++;
        } else {
            missing.add("Must contain at least one special character");
        }

        Strength strength = switch (score) {
            case 5 -> Strength.VERY_STRONG;
            case 4 -> Strength.STRONG;
            case 3 -> Strength.MEDIUM;
            case 2 -> Strength.WEAK;
            default -> Strength.VERY_WEAK;
        };

        // Meets minimum production threshold if length, upper, lower, and digit/special are present
        boolean isValid = score >= 4 && LENGTH_PATTERN.matcher(password).matches();

        return new Result(strength, score, missing, isValid);
    }
}