package com.example.fleetmanagement.issues.interfaces.rest.resources;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;

public record UpdateIssueResource(String title, String content, IssueType type) {}
