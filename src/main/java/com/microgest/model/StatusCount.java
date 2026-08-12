package com.microgest.model;

public class StatusCount {

    private final StatutAdherent statut;
    private final long total;

    public StatusCount(StatutAdherent statut, long total) {
        this.statut = statut;
        this.total = total;
    }

    public StatutAdherent getStatut() {
        return statut;
    }

    public long getTotal() {
        return total;
    }
}
