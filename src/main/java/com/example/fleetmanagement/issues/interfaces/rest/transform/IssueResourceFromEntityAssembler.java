package com.example.fleetmanagement.issues.interfaces.rest.transform;

import com.example.fleetmanagement.issues.domain.model.aggregates.Issue;
import com.example.fleetmanagement.issues.interfaces.rest.resources.IssueResource;

import java.util.List;

public class IssueResourceFromEntityAssembler {
    public static IssueResource toResourceFromEntity(Issue entity){
        return new IssueResource(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getType(),
                entity.getReportDate(),
                entity.getCarrierId(),
                entity.getManagerId(),
                entity.getVehicleId(),
                entity.getShipmentId()
        );
    }

    public static List<IssueResource> toResourceFromEntities(List<Issue> entities){
        return entities.stream()
                .map(IssueResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }
}
