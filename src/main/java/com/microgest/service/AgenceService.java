package com.microgest.service;

import com.microgest.model.Agence;
import com.microgest.repository.AgenceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AgenceService {

    private final AgenceRepository agenceRepository;

    public AgenceService() {
        this(new AgenceRepository());
    }

    public AgenceService(AgenceRepository agenceRepository) {
        this.agenceRepository = agenceRepository;
    }

    public int create(Agence agence) {
        requireText(agence.getNom(), "Le nom de l'agence est obligatoire");
        if (agence.getDateCreation() == null) {
            agence.setDateCreation(LocalDateTime.now());
        }
        if (agence.getActif() == null) {
            agence.setActif(Boolean.TRUE);
        }
        return agenceRepository.create(agence);
    }

    public boolean update(Agence agence) {
        requireId(agence.getId(), "L'identifiant de l'agence est obligatoire");
        requireText(agence.getNom(), "Le nom de l'agence est obligatoire");
        return agenceRepository.update(agence);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant de l'agence est obligatoire");
        return agenceRepository.delete(id);
    }

    public Optional<Agence> findById(Integer id) {
        return agenceRepository.findById(id);
    }

    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    public Optional<Agence> findByNom(String nom) {
        return agenceRepository.findByNom(nom);
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