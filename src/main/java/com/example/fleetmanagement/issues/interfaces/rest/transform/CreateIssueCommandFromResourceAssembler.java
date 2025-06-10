package com.example.fleetmanagement.issues.interfaces.rest.transform;

import com.example.fleetmanagement.issues.domain.model.commands.CreateIssueCommand;
import com.example.fleetmanagement.issues.interfaces.rest.resources.CreateIssueResource;

import java.time.LocalDateTime;

public class CreateIssueCommandFromResourceAssembler {
    public static CreateIssueCommand toCommandFromResource(CreateIssueResource resource, Long carrierId, Long managerId) {
        return new CreateIssueCommand(resource.title(),
                resource.content(),
                resource.type(),
                LocalDateTime.now(),
                carrierId,
                managerId,
                resource.vehicleId(),
                resource.shipmentId());
    }
}

