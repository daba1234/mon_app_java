package com.microgest.service;

import com.microgest.model.Pret;
import com.microgest.model.StatutPret;
import com.microgest.repository.AdherentRepository;
import com.microgest.repository.PretRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PretService {

    private final PretRepository pretRepository;
    private final AdherentRepository adherentRepository;

    public PretService() {
        this(new PretRepository(), new AdherentRepository());
    }

    public PretService(PretRepository pretRepository, AdherentRepository adherentRepository) {
        this.pretRepository = pretRepository;
        this.adherentRepository = adherentRepository;
    }

    public int create(Pret pret) {
        requireId(pret.getAdherentId(), "L'adhérent du prêt est obligatoire");
        requireAmount(pret.getMontantDemande(), "Le montant demandé est obligatoire");
        requireAmount(pret.getTauxInteret(), "Le taux d'intérêt est obligatoire");
        adherentRepository.findById(pret.getAdherentId())
                .orElseThrow(() -> new IllegalArgumentException("L'adhérent indiqué n'existe pas"));
        if (pret.getStatut() == null) {
            pret.setStatut(StatutPret.EN_ATTENTE);
        }
        if (pret.getDateDebut() == null) {
            pret.setDateDebut(LocalDate.now());
        }
        if (pret.getDateFin() == null) {
            pret.setDateFin(LocalDate.now().plusMonths(1));
        }
        return pretRepository.create(pret);
    }

    public boolean update(Pret pret) {
        requireId(pret.getId(), "L'identifiant du prêt est obligatoire");
        requireId(pret.getAdherentId(), "L'adhérent du prêt est obligatoire");
        requireAmount(pret.getMontantDemande(), "Le montant demandé est obligatoire");
        requireAmount(pret.getTauxInteret(), "Le taux d'intérêt est obligatoire");
        return pretRepository.update(pret);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant du prêt est obligatoire");
        return pretRepository.delete(id);
    }

    public Optional<Pret> findById(Integer id) {
        return pretRepository.findById(id);
    }

    public List<Pret> findAll() {
        return pretRepository.findAll();
    }

    public List<Pret> findByAdherentId(Integer adherentId) {
        requireId(adherentId, "L'identifiant de l'adhérent est obligatoire");
        return pretRepository.findByAdherentId(adherentId);
    }

    public boolean approuverPret(Integer pretId, BigDecimal montantAccorde) {
        requireId(pretId, "L'identifiant du prêt est obligatoire");
        requireAmount(montantAccorde, "Le montant accordé est obligatoire");
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new IllegalArgumentException("Le prêt indiqué n'existe pas"));
        pret.setStatut(StatutPret.APPROUVE);
        pret.setMontantAccorde(montantAccorde);
        pret.setMotifRejet(null);
        return pretRepository.update(pret);
    }

    public boolean rejeterPret(Integer pretId, String motifRejet) {
        requireId(pretId, "L'identifiant du prêt est obligatoire");
        if (motifRejet == null || motifRejet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif du rejet est obligatoire");
        }
        Pret pret = pretRepository.findById(pretId)
                .orElseThrow(() -> new IllegalArgumentException("Le prêt indiqué n'existe pas"));
        pret.setStatut(StatutPret.REJETE);
        pret.setMotifRejet(motifRejet);
        return pretRepository.update(pret);
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