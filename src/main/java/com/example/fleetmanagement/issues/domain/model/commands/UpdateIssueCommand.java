package com.example.fleetmanagement.issues.domain.model.commands;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;

public record UpdateIssueCommand(String title, String content, IssueType type) {}
