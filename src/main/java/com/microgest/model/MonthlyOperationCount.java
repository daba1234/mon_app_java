package com.microgest.model;

public class MonthlyOperationCount {

    private final String monthLabel;
    private final long total;

    public MonthlyOperationCount(String monthLabel, long total) {
        this.monthLabel = monthLabel;
        this.total = total;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public long getTotal() {
        return total;
    }
}
