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
@Table(name = "remboursement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pret_id", nullable = false)
    private Integer pretId;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "date_remboursement", nullable = false)
    private LocalDate dateRemboursement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutRemboursement statut;
}