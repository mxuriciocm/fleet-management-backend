package com.example.fleetmanagement.analytics.interfaces.rest.transform;

import com.example.fleetmanagement.analytics.interfaces.acl.dto.CarrierPerformanceDto;
import com.example.fleetmanagement.analytics.interfaces.rest.resources.CarrierPerformanceResource;

import java.util.List;

public class CarrierPerformanceResourceFromDtoAssembler {
    public static CarrierPerformanceResource toResourceFromDto(CarrierPerformanceDto dto) {
        return new CarrierPerformanceResource(
                dto.carrierId(),
                dto.carrierName(),
                dto.totalShipments(),
                dto.completedShipments(),
                dto.inProgressShipments(),
                dto.pendingShipments(),
                dto.cancelledShipments(),
                dto.completionRate(),
                dto.totalReports(),
                dto.vehicleReports(),
                dto.routeReports(),
                dto.technicalIssueReports(),
                dto.lastShipmentDate(),
                dto.vehicleInfo()
        );
    }

    public static List<CarrierPerformanceResource> toResourcesFromDtos(List<CarrierPerformanceDto> dtos) {
        return dtos.stream()
                .map(CarrierPerformanceResourceFromDtoAssembler::toResourceFromDto)
                .toList();
    }
}


