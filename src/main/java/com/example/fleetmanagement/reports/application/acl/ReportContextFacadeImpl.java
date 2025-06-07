package com.example.fleetmanagement.reports.application.acl;

import com.example.fleetmanagement.reports.domain.model.aggregates.Report;
import com.example.fleetmanagement.reports.domain.model.queries.*;
import com.example.fleetmanagement.reports.domain.model.valueobjects.ReportType;
import com.example.fleetmanagement.reports.domain.services.ReportQueryService;
import com.example.fleetmanagement.reports.interfaces.acl.ReportContextFacade;
import com.example.fleetmanagement.reports.interfaces.acl.dto.ReportDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportContextFacadeImpl implements ReportContextFacade {
    private final ReportQueryService reportQueryService;

    public ReportContextFacadeImpl(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }

    /**
     * Fetches a report by its ID.
     * @param reportId the ID of the report to fetch
     * @return an Optional containing the ReportDto if found, or empty if not found
     */
    @Override
    public Optional<ReportDto> fetchReportById(Long reportId) {
        return reportQueryService.handle(new GetReportByIdQuery(reportId))
            .map(this::mapToDto);
    }

    /**
     * Fetches all reports associated with a specific carrier ID.
     * @param carrierId the ID of the carrier whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given carrier ID
     */
    @Override
    public List<ReportDto> fetchReportsByCarrierId(Long carrierId) {
        return reportQueryService.handle(new GetReportsByCarrierIdQuery(carrierId))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Fetches all reports associated with a specific manager ID.
     * @param managerId the ID of the manager whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given manager ID
     */
    @Override
    public List<ReportDto> fetchReportsByManagerId(Long managerId) {
        return reportQueryService.handle(new GetReportsByManagerIdQuery(managerId))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Fetches all reports of a specific type.
     * @param type the type of reports to fetch
     * @return a list of ReportDto objects of the specified type
     */
    @Override
    public List<ReportDto> fetchReportsByType(ReportType type) {
        return reportQueryService.handle(new GetReportsByTypeQuery(type))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Fetches all reports associated with a specific vehicle ID.
     * @param vehicleId the ID of the vehicle whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given vehicle ID
     */
    @Override
    public List<ReportDto> fetchReportsByVehicleId(Long vehicleId) {
        return reportQueryService.handle(new GetReportsByVehicleIdQuery(vehicleId))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Fetches all reports associated with a specific shipment ID.
     * @param shipmentId the ID of the shipment whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given shipment ID
     */
    @Override
    public List<ReportDto> fetchReportsByShipmentId(Long shipmentId) {
        return reportQueryService.handle(new GetReportsByShipmentIdQuery(shipmentId))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Maps a Report entity to a ReportDto.
     * @param report the Report entity to map
     * @return a ReportDto containing the mapped data
     */
    private ReportDto mapToDto(Report report) {
        return new ReportDto(
            report.getId(),
            report.getTitle(),
            report.getContent(),
            report.getType(),
            report.getReportDate(),
            report.getCarrierId(),
            report.getManagerId(),
            report.getVehicleId(),
            report.getShipmentId()
        );
    }
}
