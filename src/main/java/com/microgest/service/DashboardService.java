package com.microgest.service;

import com.microgest.repository.DashboardRepository;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService() {
        this(new DashboardRepository());
    }

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public long totalAdherents() {
        return dashboardRepository.countTotalAdherents();
    }

    public long adherentsActifs() {
        return dashboardRepository.countAdherentsActifs();
    }

    public long operationsDuMois() {
        return dashboardRepository.countOperationsCurrentMonth();
    }

    public BigDecimal totalEpargne() {
        return dashboardRepository.totalEpargne();
    }

    public Map<String, Long> adherentsByStatus() {
        return dashboardRepository.adherentsByStatus();
    }

    public Map<String, Long> operationsByMonth() {
        return dashboardRepository.operationsByMonth();
    }
}