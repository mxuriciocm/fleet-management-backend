package com.example.fleetmanagement.analytics.domain.services;


import com.example.fleetmanagement.analytics.interfaces.acl.dto.CarrierPerformanceDto;

import java.util.List;
import java.util.Optional;

public interface AnalyticQueryService {
    Optional<CarrierPerformanceDto> getCarrierPerformance(Long carrierId);

    List<CarrierPerformanceDto> getTopPerformingCarriers(Long managerId, int limit, String metric);

    List<CarrierPerformanceDto> getLowPerformingCarriers(Long managerId, int limit, String metric);
}
