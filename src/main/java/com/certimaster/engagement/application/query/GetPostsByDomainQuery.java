package com.certimaster.engagement.application.query;

public record GetPostsByDomainQuery(Long certificationId, Long domainId, int page, int size) {
}
