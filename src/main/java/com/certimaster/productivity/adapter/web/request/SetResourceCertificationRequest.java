package com.certimaster.productivity.adapter.web.request;

/** Attach a resource to a certification; null/absent {@code certificationId} detaches it. */
public class SetResourceCertificationRequest {

    private Long certificationId;

    public SetResourceCertificationRequest() {
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }
}
