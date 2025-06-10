package com.example.fleetmanagement.issues.domain.model.queries;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;

public record GetIssuesByTypeQuery(IssueType type) {}
