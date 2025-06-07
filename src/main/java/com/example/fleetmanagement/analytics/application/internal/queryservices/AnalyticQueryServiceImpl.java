package com.example.fleetmanagement.analytics.application.internal.queryservices;

import com.example.fleetmanagement.analytics.domain.model.valueobjects.PerformanceMetric;
import com.example.fleetmanagement.analytics.domain.model.valueobjects.SortDirection;
import com.example.fleetmanagement.analytics.domain.services.AnalyticQueryService;
import com.example.fleetmanagement.analytics.interfaces.acl.dto.CarrierPerformanceDto;
import com.example.fleetmanagement.iam.interfaces.acl.IamContextFacade;
import com.example.fleetmanagement.profile.interfaces.acl.ProfileContextFacade;
import com.example.fleetmanagement.profile.interfaces.acl.dto.ProfileDto;
import com.example.fleetmanagement.reports.interfaces.acl.ReportContextFacade;
import com.example.fleetmanagement.shipments.interfaces.acl.ShipmentContextFacade;
import com.example.fleetmanagement.vehicles.interfaces.acl.VehicleContextFacade;
import com.example.fleetmanagement.vehicles.interfaces.acl.dto.VehicleDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticQueryServiceImpl implements AnalyticQueryService {
    private final VehicleContextFacade vehicleContextFacade;
    private final ShipmentContextFacade shipmentContextFacade;
    private final ReportContextFacade reportContextFacade;
    private final IamContextFacade iamContextFacade;
    private final ProfileContextFacade profileContextFacade;

    public AnalyticQueryServiceImpl(
            VehicleContextFacade vehicleContextFacade,
            ShipmentContextFacade shipmentContextFacade,
            ReportContextFacade reportContextFacade,
            IamContextFacade iamContextFacade,
            ProfileContextFacade profileContextFacade) {
        this.vehicleContextFacade = vehicleContextFacade;
        this.shipmentContextFacade = shipmentContextFacade;
        this.reportContextFacade = reportContextFacade;
        this.iamContextFacade = iamContextFacade;
        this.profileContextFacade = profileContextFacade;
    }

    /**
     * Retrieves the performance metrics for a specific carrier.
     * @param carrierId the ID of the carrier
     * @return an Optional containing CarrierPerformanceDto if found, otherwise empty
     */
    @Override
    public Optional<CarrierPerformanceDto> getCarrierPerformance(Long carrierId){
        var carrierUser = iamContextFacade.fetchUserById(carrierId);
        if (carrierUser.isEmpty() || !carrierUser.get().roles().contains("ROLE_CARRIER")) {
            return Optional.empty();
        }
        var profileDataOptional = profileContextFacade.fetchProfileByUserId(carrierId);
        String carrierName = profileDataOptional.map(ProfileDto::fullName).orElse("Unknown Carrier");
        var shipments = shipmentContextFacade.fetchShipmentsByCarrierId(carrierId);
        var totalShipments = shipments.size();
        var completedShipments = 0;
        var inProgressShipments = 0;
        var pendingShipments = 0;
        var canceledShipments = 0;
        var lastShipmentDate = LocalDate.now().minusMonths(1);

        for (var shipment : shipments) {
            switch (shipment.status().toString()) {
                case "COMPLETED" -> completedShipments++;
                case "IN_PROGRESS" -> inProgressShipments++;
                case "PENDING" -> pendingShipments++;
                case "CANCELED" -> canceledShipments++;
            }

            LocalDate shipmentDate = null;

            if (shipment.completedDate() != null) { shipmentDate = shipment.completedDate().toLocalDate(); }
            else if (shipment.startedDate() != null) { shipmentDate = shipment.startedDate().toLocalDate(); }
            else if (shipment.scheduledDate() != null) { shipmentDate = shipment.scheduledDate().toLocalDate();}

            if (shipmentDate != null && shipmentDate.isAfter(lastShipmentDate)) {
                lastShipmentDate = shipmentDate;
            }
        }

        var reports = reportContextFacade.fetchReportsByCarrierId(carrierId);
        var totalReports = reports.size();

        int vehicleReports = 0;
        int routeReports = 0;
        int technicalIssueReports = 0;

        for (var report : reports) {
            switch (report.type().toString()) {
                case "VEHICLE" -> vehicleReports++;
                case "ROUTE" -> routeReports++;
                case "TECHNICAL" -> technicalIssueReports++;
            }
        }

        double completionRate = totalShipments > 0 ?
                (double) completedShipments / totalShipments * 100 : 0;

        var vehicleOptional = vehicleContextFacade.fetchVehicleByCarrierId(carrierId);
        String vehicleInfo = vehicleOptional.map(vehicleDto -> vehicleDto.model() + " (" + vehicleDto.licensePlate() + ")").orElse("Not Assigned");

        return Optional.of(new CarrierPerformanceDto(
                carrierId,
                carrierName,
                totalShipments,
                completedShipments,
                inProgressShipments,
                pendingShipments,
                canceledShipments,
                completionRate,
                totalReports,
                vehicleReports,
                routeReports,
                technicalIssueReports,
                lastShipmentDate,
                vehicleInfo
        ));
    }

    /**
     * Retrieves a list of carriers sorted by a specific performance metric.
     * @param managerId the ID of the manager requesting the data
     * @param limit the maximum number of carriers to return
     * @param metric the performance metric to sort by
     * @param direction the direction to sort the results (ascending or descending)
     * @return a list of CarrierPerformanceDto sorted by the specified metric
     */
    @Override
    public List<CarrierPerformanceDto> getCarriersByMetric(Long managerId, int limit, PerformanceMetric metric, SortDirection direction){
        var managerUser = iamContextFacade.fetchUserById(managerId);
        if (managerUser.isEmpty() || !managerUser.get().roles().contains("ROLE_MANAGER")) {
            return List.of();
        }

        var vehicles = vehicleContextFacade.fetchVehiclesByManagerId(managerId);
        List<Long> carrierIds = vehicles.stream()
                .map(VehicleDto::carrierId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        List<CarrierPerformanceDto> performances = new ArrayList<>();
        for (Long carrierId : carrierIds) {
            var performanceOptional = getCarrierPerformance(carrierId);
            performanceOptional.ifPresent(performances::add);
        }

        Comparator<CarrierPerformanceDto> comparator = getComparatorByMetric(metric);

        if (direction == SortDirection.ASC) {
            comparator = comparator.reversed();
        }

        return performances.stream()
                .sorted(comparator)
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .toList();
    }

    private Comparator<CarrierPerformanceDto> getComparatorByMetric(PerformanceMetric metric) {
        return switch (metric) {
            case TOTAL_SHIPMENTS -> Comparator.comparingInt(CarrierPerformanceDto::totalShipments);
            case COMPLETED_SHIPMENTS -> Comparator.comparingInt(CarrierPerformanceDto::completedShipments);
            case TOTAL_REPORTS -> Comparator.comparingInt(CarrierPerformanceDto::totalReports);
            default -> Comparator.comparing(CarrierPerformanceDto::completionRate).reversed();
        };
    }

}
