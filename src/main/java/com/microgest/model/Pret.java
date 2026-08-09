package com.microgest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pret")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "adherent_id", nullable = false)
    private Integer adherentId;

    @Column(name = "montant_demande", nullable = false)
    private BigDecimal montantDemande;

    @Column(name = "montant_accorde")
    private BigDecimal montantAccorde;

    @Column(name = "taux_interet", nullable = false)
    private BigDecimal tauxInteret;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutPret statut;

    @Column(name = "motif_rejet")
    private String motifRejet;
}