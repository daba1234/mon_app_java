package com.microgest.model;

public class AdherentSearchCriteria {

    private String searchTerm;
    private StatutAdherent statut;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public StatutAdherent getStatut() {
        return statut;
    }

    public void setStatut(StatutAdherent statut) {
        this.statut = statut;
    }
}
