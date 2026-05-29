package com.certimaster.learning.application.query;

public record SearchCertificationsFullTextQuery(String query, int page, int size) {
}
