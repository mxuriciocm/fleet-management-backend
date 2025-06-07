package com.example.fleetmanagement.analytics.domain.model.valueobjects;

import lombok.Getter;

@Getter
public enum SortDirection {
    ASC("asc", "Ascending"),
    DESC("desc", "Descending");

    private final String code;
    private final String description;

    SortDirection(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SortDirection fromCode(String code) {
        if (code == null || code.isEmpty()) { return DESC; }
        for (SortDirection direction : SortDirection.values()) {
            if (direction.getCode().equalsIgnoreCase(code)) {
                return direction;
            }
        }
        return DESC;
    }
}
