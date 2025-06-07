package com.example.fleetmanagement.analytics.interfaces.acl.dto;

import java.time.LocalDate;

public record CarrierPerformanceDto(
        Long carrierId,
        String carrierName,
        int totalShipments,
        int completedShipments,
        int inProgressShipments,
        int pendingShipments,
        int cancelledShipments,
        double completionRate,
        int totalReports,
        LocalDate lastShipmentDate
) {
}

