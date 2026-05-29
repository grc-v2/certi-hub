package com.certimaster.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing user input to prevent SQL injection and XSS attacks.
 * This class provides methods to escape special characters and encode HTML entities.
 */
@Component
public class InputSanitizer {

    private static final Logger log = LoggerFactory.getLogger(InputSanitizer.class);

    // SQL injection patterns to detect
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('.*(--|;|/\\*|\\*/|xp_|sp_|exec|execute|select|insert|update|delete|drop|create|alter|union|script).*')|" +
        "(\\b(select|insert|update|delete|drop|create|alter|union|exec|execute|script)\\b)",
        Pattern.CASE_INSENSITIVE
    );

    // XSS patterns to detect
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(<script[^>]*>.*?</script>)|" +
        "(<iframe[^>]*>.*?</iframe>)|" +
        "(javascript:)|" +
        "(on\\w+\\s*=)|" +
        "(<[^>]*\\s+on\\w+\\s*=)",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Sanitizes a string input by escaping special characters and encoding HTML entities.
     * Returns null if input is null.
     *
     * @param input the string to sanitize
     * @return sanitized string or null if input is null
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }

        // Trim whitespace
        String sanitized = input.trim();

        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            log.warn("Potential SQL injection attempt detected: {}", sanitized.substring(0, Math.min(50, sanitized.length())));
        }

        // Check for XSS patterns
        if (XSS_PATTERN.matcher(sanitized).find()) {
            log.warn("Potential XSS attempt detected: {}", sanitized.substring(0, Math.min(50, sanitized.length())));
        }

        // Encode HTML entities
        sanitized = encodeHtmlEntities(sanitized);

        // Escape SQL special characters (for additional safety, though JPA handles this)
        sanitized = escapeSqlSpecialCharacters(sanitized);

        return sanitized;
    }

    /**
     * Encodes HTML entities to prevent XSS attacks.
     *
     * @param input the string to encode
     * @return encoded string
     */
    private String encodeHtmlEntities(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder encoded = new StringBuilder(input.length() * 2);
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<':
                    encoded.append("&lt;");
                    break;
                case '>':
                    encoded.append("&gt;");
                    break;
                case '&':
                    encoded.append("&amp;");
                    break;
                case '"':
                    encoded.append("&quot;");
                    break;
                case '\'':
                    encoded.append("&#x27;");
                    break;
                case '/':
                    encoded.append("&#x2F;");
                    break;
                default:
                    encoded.append(c);
            }
        }
        return encoded.toString();
    }

    /**
     * Escapes SQL special characters to prevent SQL injection.
     * Note: JPA/Hibernate handles parameterized queries, but this provides additional safety.
     *
     * @param input the string to escape
     * @return escaped string
     */
    private String escapeSqlSpecialCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Replace single quotes with two single quotes (SQL escape)
        String escaped = input.replace("'", "''");

        // Remove or escape other potentially dangerous characters
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\0", "");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\t", "\\t");

        return escaped;
    }

    /**
     * Sanitizes email addresses by removing potentially dangerous characters
     * while preserving valid email format.
     *
     * @param email the email to sanitize
     * @return sanitized email or null if input is null
     */
    public String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }

        // Trim and convert to lowercase
        String sanitized = email.trim().toLowerCase();

        // Remove any HTML tags or scripts
        sanitized = sanitized.replaceAll("<[^>]*>", "");

        // Remove any non-email characters (keep alphanumeric, @, ., -, _)
        sanitized = sanitized.replaceAll("[^a-z0-9@._-]", "");

        return sanitized;
    }

    /**
     * Sanitizes phone numbers by removing all non-numeric characters except + at the start.
     *
     * @param phone the phone number to sanitize
     * @return sanitized phone number or null if input is null
     */
    public String sanitizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        // Trim whitespace
        String sanitized = phone.trim();

        // Keep only digits and + at the start
        if (sanitized.startsWith("+")) {
            sanitized = "+" + sanitized.substring(1).replaceAll("[^0-9]", "");
        } else {
            sanitized = sanitized.replaceAll("[^0-9]", "");
        }

        return sanitized;
    }

    /**
     * Validates that a string does not contain SQL injection patterns.
     *
     * @param input the string to validate
     * @return true if input is safe, false if potential SQL injection detected
     */
    public boolean isSafeSql(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        return !SQL_INJECTION_PATTERN.matcher(input).find();
    }

    /**
     * Validates that a string does not contain XSS patterns.
     *
     * @param input the string to validate
     * @return true if input is safe, false if potential XSS detected
     */
    public boolean isSafeXss(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        return !XSS_PATTERN.matcher(input).find();
    }

    /**
     * Sanitizes a search query by removing special characters that could be used
     * for SQL injection while preserving useful search characters.
     *
     * @param query the search query to sanitize
     * @return sanitized query or null if input is null
     */
    public String sanitizeSearchQuery(String query) {
        if (query == null) {
            return null;
        }

        // Trim whitespace
        String sanitized = query.trim();

        // Remove SQL keywords and dangerous patterns
        sanitized = sanitized.replaceAll("(?i)(--|;|/\\*|\\*/|xp_|sp_|exec|execute|union|script)", "");

        // Encode HTML entities
        sanitized = encodeHtmlEntities(sanitized);

        // Limit length to prevent DoS
        if (sanitized.length() > 300) {
            sanitized = sanitized.substring(0, 300);
            log.warn("Search query truncated to 300 characters");
        }

        return sanitized;
    }
}
