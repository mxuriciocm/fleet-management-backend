package com.example.fleetmanagement.issues.interfaces.acl.dto;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;

import java.time.LocalDateTime;

public record IssueDto(
        Long id,
        String title,
        String content,
        IssueType type,
        LocalDateTime reportDate,
        Long carrierId,
        Long managerId,
        Long vehicleId,
        Long shipmentId
) {}
