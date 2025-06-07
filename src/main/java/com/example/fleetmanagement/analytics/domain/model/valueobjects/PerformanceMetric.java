package com.example.fleetmanagement.analytics.domain.model.valueobjects;

import lombok.Getter;

@Getter
public enum PerformanceMetric {
    COMPLETION_RATE("completionRate", "Percentage of completed shipments"),
    TOTAL_SHIPMENTS("totalShipments", "Total number of shipments"),
    COMPLETED_SHIPMENTS("completedShipments", "Total number of completed shipments"),
    TOTAL_REPORTS("totalReports", "Total number of reports"),
    ;

    private final String code;
    private final String description;

    PerformanceMetric(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PerformanceMetric fromCode(String code) {
        if (code == null || code.trim().isEmpty()) { return COMPLETION_RATE; }
        for (PerformanceMetric metric : values()) {
            if (metric.getCode().equalsIgnoreCase(code)) {
                return metric;
            }
        }
        return COMPLETION_RATE;
    }
}
