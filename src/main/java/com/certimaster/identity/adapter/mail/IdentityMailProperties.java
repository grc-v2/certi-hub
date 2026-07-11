package com.certimaster.identity.adapter.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration for transactional emails sent by the identity module.
 * SMTP connectivity itself is configured through the standard {@code spring.mail.*} properties.
 */
@Component
@ConfigurationProperties(prefix = "app.mail")
public class IdentityMailProperties {

    /**
     * Feature flag. When disabled, emails are logged instead of sent.
     */
    private boolean enabled = true;

    /**
     * From address used for all transactional emails.
     */
    private String from = "no-reply@certimaster.com";

    /**
     * Base URL of the frontend, used to build verification / reset links.
     */
    private String frontendBaseUrl = "http://localhost:3000";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public void setFrontendBaseUrl(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }
}
