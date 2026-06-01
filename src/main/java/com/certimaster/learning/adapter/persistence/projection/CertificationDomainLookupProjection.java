package com.certimaster.learning.adapter.persistence.projection;

public interface CertificationDomainLookupProjection {

    Long getId();

    String getName();

    String getCode();

    Long getCertificationId();

    Long getParentDomainId();

    Integer getDisplayOrder();

    String getStatus();
}
