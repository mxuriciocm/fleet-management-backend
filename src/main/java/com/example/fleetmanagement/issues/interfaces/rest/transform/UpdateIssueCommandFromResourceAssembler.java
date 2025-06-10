package com.example.fleetmanagement.issues.interfaces.rest.transform;

import com.example.fleetmanagement.issues.domain.model.commands.UpdateIssueCommand;
import com.example.fleetmanagement.issues.interfaces.rest.resources.UpdateIssueResource;

public class UpdateIssueCommandFromResourceAssembler {
    public static UpdateIssueCommand toCommandFromResource(UpdateIssueResource resource) {
        return new UpdateIssueCommand(resource.title(), resource.content(), resource.type());
    }
}
