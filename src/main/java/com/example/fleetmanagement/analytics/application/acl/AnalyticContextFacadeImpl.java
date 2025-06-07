package com.example.fleetmanagement.analytics.application.acl;

import com.example.fleetmanagement.analytics.domain.model.valueobjects.PerformanceMetric;
import com.example.fleetmanagement.analytics.domain.model.valueobjects.SortDirection;
import com.example.fleetmanagement.analytics.domain.services.AnalyticQueryService;
import com.example.fleetmanagement.analytics.interfaces.acl.AnalyticContextFacade;
import com.example.fleetmanagement.analytics.interfaces.acl.dto.CarrierPerformanceDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyticContextFacadeImpl implements AnalyticContextFacade {
    private final AnalyticQueryService analyticQueryService;

    public AnalyticContextFacadeImpl(AnalyticQueryService analyticQueryService) {
        this.analyticQueryService = analyticQueryService;
    }

    @Override
    public Optional<CarrierPerformanceDto> getCarrierPerformance(Long carrierId) {
        return analyticQueryService.getCarrierPerformance(carrierId);
    }

    @Override
    public List<CarrierPerformanceDto> getCarriersByMetric(Long managerId, int limit, PerformanceMetric metric, SortDirection sortDirection) {
        return analyticQueryService.getCarriersByMetric(managerId, limit, metric, sortDirection);
    }
}
