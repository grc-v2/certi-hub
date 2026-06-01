package com.certimaster.learning.adapter.persistence.projection;

public interface CertificationLookupProjection {

    Long getId();

    String getName();

    String getCode();

    String getSlug();

    String getProvider();

    String getProviderName();

    String getProviderLogo();

    String getStatus();
}
