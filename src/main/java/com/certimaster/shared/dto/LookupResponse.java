package com.certimaster.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LookupResponse(
        Map<String, List<LookupOption>> constants,
        Map<String, List<LookupOption>> catalogs
) {

    public LookupResponse {
        constants = constants == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(constants));
        catalogs = catalogs == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(catalogs));
    }
}
