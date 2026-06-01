package com.certimaster.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LookupOption(
        String value,
        String label,
        Map<String, Object> metadata
) {

    public LookupOption {
        metadata = metadata == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(metadata));
    }

    public static LookupOption of(String value, String label) {
        return new LookupOption(value, label, Map.of());
    }

    public static LookupOption of(String value, String label, Map<String, Object> metadata) {
        return new LookupOption(value, label, metadata);
    }
}
