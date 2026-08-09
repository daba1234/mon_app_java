package com.microgest.service;

import com.microgest.model.Pret;
import com.microgest.model.Remboursement;
import com.microgest.model.StatutPret;
import com.microgest.model.StatutRemboursement;
import com.microgest.repository.PretRepository;
import com.microgest.repository.RemboursementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final PretRepository pretRepository;

    public RemboursementService() {
        this(new RemboursementRepository(), new PretRepository());
    }

    public RemboursementService(RemboursementRepository remboursementRepository, PretRepository pretRepository) {
        this.remboursementRepository = remboursementRepository;
        this.pretRepository = pretRepository;
    }

    public int create(Remboursement remboursement) {
        requireId(remboursement.getPretId(), "Le prêt du remboursement est obligatoire");
        requireAmount(remboursement.getMontant(), "Le montant du remboursement est obligatoire");
        Pret pret = pretRepository.findById(remboursement.getPretId())
                .orElseThrow(() -> new IllegalArgumentException("Le prêt indiqué n'existe pas"));
        if (remboursement.getStatut() == null) {
            remboursement.setStatut(StatutRemboursement.PAYE);
        }
        if (remboursement.getDateRemboursement() == null) {
            remboursement.setDateRemboursement(LocalDate.now());
        }
        int remboursementId = remboursementRepository.create(remboursement);
        BigDecimal total = remboursementRepository.sumMontantByPretId(pret.getId());
        BigDecimal cible = pret.getMontantAccorde() != null ? pret.getMontantAccorde() : pret.getMontantDemande();
        if (cible != null && total.compareTo(cible) >= 0) {
            pret.setStatut(StatutPret.REMBOURSE);
            pretRepository.update(pret);
        }
        return remboursementId;
    }

    public boolean update(Remboursement remboursement) {
        requireId(remboursement.getId(), "L'identifiant du remboursement est obligatoire");
        requireId(remboursement.getPretId(), "Le prêt du remboursement est obligatoire");
        requireAmount(remboursement.getMontant(), "Le montant du remboursement est obligatoire");
        return remboursementRepository.update(remboursement);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant du remboursement est obligatoire");
        return remboursementRepository.delete(id);
    }

    public Optional<Remboursement> findById(Integer id) {
        return remboursementRepository.findById(id);
    }

    public List<Remboursement> findAll() {
        return remboursementRepository.findAll();
    }

    public List<Remboursement> findByPretId(Integer pretId) {
        requireId(pretId, "L'identifiant du prêt est obligatoire");
        return remboursementRepository.findByPretId(pretId);
    }

    private void requireId(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireAmount(BigDecimal value, String message) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}