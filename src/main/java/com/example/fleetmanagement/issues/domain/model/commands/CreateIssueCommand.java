package com.example.fleetmanagement.issues.domain.model.commands;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;

import java.time.LocalDateTime;

public record CreateIssueCommand(String title, String content, IssueType type, LocalDateTime reportDate, Long carrierId, Long managerId, Long vehicleId, Long shipmentId) {}
