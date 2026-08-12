package com.microgest.model;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStats {

    private final long totalAdherents;
    private final long activeAdherents;
    private final long operationsThisMonth;
    private final BigDecimal totalSavings;
    private final List<StatusCount> statusCounts;
    private final List<MonthlyOperationCount> monthlyOperationCounts;

    public DashboardStats(long totalAdherents,
                          long activeAdherents,
                          long operationsThisMonth,
                          BigDecimal totalSavings,
                          List<StatusCount> statusCounts,
                          List<MonthlyOperationCount> monthlyOperationCounts) {
        this.totalAdherents = totalAdherents;
        this.activeAdherents = activeAdherents;
        this.operationsThisMonth = operationsThisMonth;
        this.totalSavings = totalSavings;
        this.statusCounts = statusCounts;
        this.monthlyOperationCounts = monthlyOperationCounts;
    }

    public long getTotalAdherents() {
        return totalAdherents;
    }

    public long getActiveAdherents() {
        return activeAdherents;
    }

    public long getOperationsThisMonth() {
        return operationsThisMonth;
    }

    public BigDecimal getTotalSavings() {
        return totalSavings;
    }

    public List<StatusCount> getStatusCounts() {
        return statusCounts;
    }

    public List<MonthlyOperationCount> getMonthlyOperationCounts() {
        return monthlyOperationCounts;
    }
}
