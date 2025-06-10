package com.example.fleetmanagement.issues.application.acl;

import com.example.fleetmanagement.issues.domain.model.aggregates.Issue;
import com.example.fleetmanagement.issues.domain.model.queries.*;
import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;
import com.example.fleetmanagement.issues.domain.services.IssueQueryService;
import com.example.fleetmanagement.issues.interfaces.acl.IssueContextFacade;
import com.example.fleetmanagement.issues.interfaces.acl.dto.IssueDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IssueContextFacadeImpl implements IssueContextFacade {
    private final IssueQueryService issueQueryService;

    public IssueContextFacadeImpl(IssueQueryService issueQueryService) {
        this.issueQueryService = issueQueryService;
    }

    /**
     * Fetches a report by its ID.
     * @param reportId the ID of the report to fetch
     * @return an Optional containing the ReportDto if found, or empty if not found
     */
    @Override
    public Optional<IssueDto> fetchReportById(Long reportId) {
        return issueQueryService.handle(new GetIssueByIdQuery(reportId))
            .map(this::mapToDto);
    }

    /**
     * Fetches all reports associated with a specific carrier ID.
     * @param carrierId the ID of the carrier whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given carrier ID
     */
    @Override
    public List<IssueDto> fetchReportsByCarrierId(Long carrierId) {
        return issueQueryService.handle(new GetIssuesByCarrierIdQuery(carrierId))
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
    public List<IssueDto> fetchReportsByManagerId(Long managerId) {
        return issueQueryService.handle(new GetIssuesByManagerIdQuery(managerId))
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
    public List<IssueDto> fetchReportsByType(IssueType type) {
        return issueQueryService.handle(new GetIssuesByTypeQuery(type))
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
    public List<IssueDto> fetchReportsByVehicleId(Long vehicleId) {
        return issueQueryService.handle(new GetIssuesByVehicleIdQuery(vehicleId))
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
    public List<IssueDto> fetchReportsByShipmentId(Long shipmentId) {
        return issueQueryService.handle(new GetIssuesByShipmentIdQuery(shipmentId))
            .stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Maps a Report entity to a ReportDto.
     * @param report the Report entity to map
     * @return a ReportDto containing the mapped data
     */
    private IssueDto mapToDto(Issue report) {
        return new IssueDto(
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
