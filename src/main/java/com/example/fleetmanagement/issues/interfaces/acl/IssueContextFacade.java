package com.example.fleetmanagement.issues.interfaces.acl;

import com.example.fleetmanagement.issues.domain.model.valueobjects.IssueType;
import com.example.fleetmanagement.issues.interfaces.acl.dto.IssueDto;

import java.util.List;
import java.util.Optional;

public interface IssueContextFacade {

    /**
     * Fetches a report by its ID.
     * @param reportId the ID of the report to fetch
     * @return an Optional containing the ReportDto if found, or empty if not found
     */
    Optional<IssueDto> fetchReportById(Long reportId);

    /**
     * Fetches all reports associated with a specific carrier ID.
     * @param carrierId the ID of the carrier whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given carrier ID
     */
    List<IssueDto> fetchReportsByCarrierId(Long carrierId);

    /**
     * Fetches all reports associated with a specific manager ID.
     * @param managerId the ID of the manager whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given manager ID
     */
    List<IssueDto> fetchReportsByManagerId(Long managerId);

    /**
     * Fetches all reports of a specific type.
     * @param type the type of reports to fetch
     * @return a list of ReportDto objects of the specified type
     */
    List<IssueDto> fetchReportsByType(IssueType type);

    /**
     * Fetches all reports associated with a specific vehicle ID.
     * @param vehicleId the ID of the vehicle whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given vehicle ID
     */
    List<IssueDto> fetchReportsByVehicleId(Long vehicleId);

    /**
     * Fetches all reports associated with a specific shipment ID.
     * @param shipmentId the ID of the shipment whose reports are to be fetched
     * @return a list of ReportDto objects associated with the given shipment ID
     */
    List<IssueDto> fetchReportsByShipmentId(Long shipmentId);
}
