package com.microgest.service;

import com.microgest.model.Adherent;
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
        requireText(adherent.getNom(), "Le nom de l'adhérent est obligatoire");
        requireText(adherent.getPrenom(), "Le prénom de l'adhérent est obligatoire");
        requireId(adherent.getAgenceId(), "L'agence de l'adhérent est obligatoire");
        agenceRepository.findById(adherent.getAgenceId())
                .orElseThrow(() -> new IllegalArgumentException("L'agence indiquée n'existe pas"));

        if (adherent.getEmail() != null && !EmailValidator.isValid(adherent.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est invalide");
        }
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
        requireId(adherent.getId(), "L'identifiant de l'adhérent est obligatoire");
        requireText(adherent.getNom(), "Le nom de l'adhérent est obligatoire");
        requireText(adherent.getPrenom(), "Le prénom de l'adhérent est obligatoire");
        requireId(adherent.getAgenceId(), "L'agence de l'adhérent est obligatoire");
        if (adherent.getEmail() != null && !EmailValidator.isValid(adherent.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est invalide");
        }
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