package com.certimaster.identity.adapter.persistence.projection;

import java.time.LocalDateTime;

public interface UserSummaryProjection {

    Long getId();

    String getEmail();

    String getFullName();

    String getUsername();

    String getStatus();

    String getRoleCodes();

    LocalDateTime getRegistrationDate();

    LocalDateTime getLastLoginDate();
}
