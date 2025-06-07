package com.example.fleetmanagement.analytics.domain.services;

import com.example.fleetmanagement.analytics.domain.model.valueobjects.PerformanceMetric;
import com.example.fleetmanagement.analytics.domain.model.valueobjects.SortDirection;
import com.example.fleetmanagement.analytics.interfaces.acl.dto.CarrierPerformanceDto;

import java.util.List;
import java.util.Optional;

public interface AnalyticQueryService {

    /**
     * Retrieves the performance metrics for a specific carrier.
     * @param carrierId the ID of the carrier
     * @return an Optional containing CarrierPerformanceDto if found, otherwise empty
     */
    Optional<CarrierPerformanceDto> getCarrierPerformance(Long carrierId);

    /**
     * Retrieves a list of carriers sorted by a specific performance metric.
     * @param managerId the ID of the manager requesting the data
     * @param limit the maximum number of carriers to return
     * @param metric the performance metric to sort by
     * @param sortDirection the direction to sort the results (ascending or descending)
     * @return a list of CarrierPerformanceDto sorted by the specified metric
     */
    List<CarrierPerformanceDto> getCarriersByMetric(Long managerId, int limit, PerformanceMetric metric, SortDirection sortDirection);
}
