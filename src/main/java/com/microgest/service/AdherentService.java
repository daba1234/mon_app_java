package com.microgest.service;

import com.microgest.model.Adherent;
import com.microgest.model.AdherentSearchCriteria;
import com.microgest.model.DashboardStats;
import com.microgest.model.PageResult;
import com.microgest.model.StatutAdherent;
import com.microgest.repository.AdherentRepository;
import com.microgest.repository.AgenceRepository;
import com.microgest.util.EmailValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AdherentService {

    private final AdherentRepository adherentRepository;
    private final AgenceRepository agenceRepository;

    public AdherentService() {
        this(new AdherentRepository(), new AgenceRepository());
    }

    public AdherentService(AdherentRepository adherentRepository, AgenceRepository agenceRepository) {
        this.adherentRepository = adherentRepository;
        this.agenceRepository = agenceRepository;
    }

    public int create(Adherent adherent) {
        validateForSave(adherent, false);
        if (adherent.getStatut() == null) {
            adherent.setStatut(StatutAdherent.ACTIF);
        }
        if (adherent.getDateAdhesion() == null) {
            adherent.setDateAdhesion(LocalDate.now());
        }
        if (adherent.getCreatedAt() == null) {
            adherent.setCreatedAt(LocalDateTime.now());
        }
        adherent.setUpdatedAt(LocalDateTime.now());
        return adherentRepository.create(adherent);
    }

    public boolean update(Adherent adherent) {
        validateForSave(adherent, true);
        adherent.setUpdatedAt(LocalDateTime.now());
        return adherentRepository.update(adherent);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant de l'adhérent est obligatoire");
        return adherentRepository.delete(id);
    }

    public Optional<Adherent> findById(Integer id) {
        return adherentRepository.findById(id);
    }

    public List<Adherent> findAll() {
        return adherentRepository.findAll();
    }

    public List<Adherent> findByAgenceId(Integer agenceId) {
        requireId(agenceId, "L'identifiant de l'agence est obligatoire");
        return adherentRepository.findByAgenceId(agenceId);
    }

    public PageResult<Adherent> findPage(String searchTerm, StatutAdherent statut, int pageNumber, int pageSize) {
        int safePageSize = validatePageSize(pageSize);
        int safePageNumber = Math.max(pageNumber, 0);
        AdherentSearchCriteria criteria = new AdherentSearchCriteria();
        criteria.setSearchTerm(searchTerm);
        criteria.setStatut(statut);

        long totalItems = adherentRepository.count(criteria);
        int lastPageNumber = totalItems == 0 ? 0 : (int) ((totalItems - 1) / safePageSize);
        safePageNumber = Math.min(safePageNumber, lastPageNumber);

        List<Adherent> adherents = adherentRepository.findPage(criteria, safePageNumber, safePageSize);
        return new PageResult<>(adherents, totalItems, safePageNumber, safePageSize);
    }

    public DashboardStats loadDashboardStats() {
        return new DashboardStats(
                adherentRepository.countAll(),
                adherentRepository.countByStatut(StatutAdherent.ACTIF),
                adherentRepository.countOperationsForCurrentMonth(),
                adherentRepository.sumCompteBalances(),
                adherentRepository.countGroupByStatut(),
                adherentRepository.countOperationsByMonth(6)
        );
    }

    private void validateForSave(Adherent adherent, boolean update) {
        if (adherent == null) {
            throw new IllegalArgumentException("Les données de l'adhérent sont obligatoires");
        }
        if (update) {
            requireId(adherent.getId(), "L'identifiant de l'adhérent est obligatoire");
        }
        requireText(adherent.getNom(), "Le nom de l'adhérent est obligatoire");
        requireText(adherent.getPrenom(), "Le prénom de l'adhérent est obligatoire");
        requireId(adherent.getAgenceId(), "L'agence de l'adhérent est obligatoire");
        agenceRepository.findById(adherent.getAgenceId())
                .orElseThrow(() -> new IllegalArgumentException("L'agence indiquée n'existe pas"));
        if (adherent.getEmail() != null && !adherent.getEmail().isBlank() && !EmailValidator.isValid(adherent.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est invalide");
        }
        if (adherent.getDateNaissance() != null && adherent.getDateNaissance().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être dans le futur");
        }
        if (adherent.getDateAdhesion() != null && adherent.getDateNaissance() != null
                && adherent.getDateAdhesion().isBefore(adherent.getDateNaissance())) {
            throw new IllegalArgumentException("La date d'adhésion ne peut pas être antérieure à la date de naissance");
        }
    }

    private int validatePageSize(int pageSize) {
        if (pageSize != 5 && pageSize != 10 && pageSize != 20 && pageSize != 50) {
            return 10;
        }
        return pageSize;
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireId(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
