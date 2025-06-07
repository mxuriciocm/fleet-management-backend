package com.example.fleetmanagement.analytics.interfaces.rest;

import com.example.fleetmanagement.analytics.domain.model.valueobjects.PerformanceMetric;
import com.example.fleetmanagement.analytics.domain.model.valueobjects.SortDirection;
import com.example.fleetmanagement.analytics.domain.services.AnalyticQueryService;
import com.example.fleetmanagement.analytics.interfaces.rest.resources.CarrierPerformanceResource;
import com.example.fleetmanagement.analytics.interfaces.rest.transform.CarrierPerformanceResourceFromDtoAssembler;
import com.example.fleetmanagement.iam.interfaces.acl.IamContextFacade;
import com.example.fleetmanagement.vehicles.interfaces.acl.VehicleContextFacade;
import com.example.fleetmanagement.vehicles.interfaces.acl.dto.VehicleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Carrier Performance Analytics")
public class AnalyticsController {
    private final AnalyticQueryService analyticQueryService;
    private final IamContextFacade iamContextFacade;
    private final VehicleContextFacade vehicleContextFacade;

    public AnalyticsController(AnalyticQueryService analyticQueryService,
                               IamContextFacade iamContextFacade,
                               VehicleContextFacade vehicleContextFacade) {
        this.analyticQueryService = analyticQueryService;
        this.iamContextFacade = iamContextFacade;
        this.vehicleContextFacade = vehicleContextFacade;
    }

    /**
     * Get all carriers performance for the authenticated manager.
     * @return ResponseEntity containing a list of CarrierPerformanceResource or no content if none found.
     */
    @GetMapping("/manager/carriers-performance")
    @Operation(summary = "Get all carriers performance for the authenticated manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved carriers performance"),
            @ApiResponse(responseCode = "204", description = "No carriers performance found for the authenticated manager"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view these statistics"),
    })
    public ResponseEntity<List<CarrierPerformanceResource>> getAllCarriersPerformance() {
        Long userId = getCurrentUserId();
        if (userId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        if (!hasRole(userId, "ROLE_MANAGER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var vehicles = vehicleContextFacade.fetchVehiclesByManagerId(userId);
        var carrierIds = vehicles.stream()
                .filter(vehicleDto -> vehicleDto.carrierId() != null)
                .map(VehicleDto::carrierId)
                .distinct()
                .toList();

        if (carrierIds.isEmpty()) { return ResponseEntity.noContent().build(); }
        var carriersPerformance = carrierIds.stream()
                .map(analyticQueryService::getCarrierPerformance)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (carriersPerformance.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = CarrierPerformanceResourceFromDtoAssembler.toResourcesFromDtos(carriersPerformance);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get performance statistics for a specific carrier.
     * @param carrierId the ID of the carrier to retrieve performance for
     * @return ResponseEntity containing CarrierPerformanceResource or no content if not found.
     */
    @GetMapping("/carrier/{carrierId}/performance")
    @Operation(summary = "Get performance statistics for a carrier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved carrier performance"),
            @ApiResponse(responseCode = "204", description = "No performance data found for the specified carrier"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view this carrier's statistics"),
    })
    public ResponseEntity<CarrierPerformanceResource> getCarrierPerformance(@PathVariable Long carrierId) {
        Long userId = getCurrentUserId();
        if (userId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        boolean isOwnStats = carrierId.equals(userId) && hasRole(userId, "ROLE_CARRIER");
        boolean canViewStats = hasRole(userId, "ROLE_ADMIN") || (hasRole(userId, "ROLE_MANAGER") && isCarrierManagedByManager(carrierId, userId));
        if (!isOwnStats && !canViewStats) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        var carrierPerformanceOptional = analyticQueryService.getCarrierPerformance(carrierId);
        if (carrierPerformanceOptional.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resource = CarrierPerformanceResourceFromDtoAssembler.toResourceFromDto(carrierPerformanceOptional.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Get carriers sorted by a specific metric for the authenticated manager.
     * @param limit the maximum number of carriers to return
     * @param metricCode the metric to sort by (e.g., "completionRate", "totalShipments")
     * @param directionCode the direction to sort (e.g., "asc", "desc")
     * @return ResponseEntity containing a list of CarrierPerformanceResource sorted by the specified metric
     */
    @GetMapping("/manager/carriers-by-metric")
    @Operation(summary = "Get carriers sorted by a specific metric for the authenticated manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved carriers sorted by metric"),
            @ApiResponse(responseCode = "204", description = "No carriers found for the authenticated manager"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view these statistics"),
    })
    public ResponseEntity<List<CarrierPerformanceResource>> getCarriersByMetric(
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam(defaultValue = "completionRate") String metricCode,
            @RequestParam(defaultValue = "desc") String directionCode) {
        Long userId = getCurrentUserId();
        if (userId == 0) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        if (!hasRole(userId, "ROLE_MANAGER")) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }

        PerformanceMetric metric = PerformanceMetric.fromCode(metricCode);
        SortDirection direction = SortDirection.fromCode(directionCode);

        var carriersPerformance = analyticQueryService.getCarriersByMetric(userId, limit, metric, direction);
        if (carriersPerformance.isEmpty()) { return ResponseEntity.noContent().build(); }
        var resources = CarrierPerformanceResourceFromDtoAssembler.toResourcesFromDtos(carriersPerformance);
        return ResponseEntity.ok(resources);
    }

    private Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { return 0L; }
        String email = authentication.getName();
        return iamContextFacade.fetchUserIdByEmail(email);
    }

    private boolean hasRole(Long userId, String role){
        var user = iamContextFacade.fetchUserById(userId);
        return user.isPresent() && user.get().roles().contains(role);
    }

    private boolean isCarrierManagedByManager(Long carrierId, Long managerId) {
        var vehicleOptional = vehicleContextFacade.fetchVehicleById(carrierId);
        return vehicleOptional.isPresent() && managerId.equals(vehicleOptional.get().managerId());
    }
}
