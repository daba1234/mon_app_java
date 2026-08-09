package com.microgest.service;

import com.microgest.model.Compte;
import com.microgest.repository.AdherentRepository;
import com.microgest.repository.CompteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CompteService {

    private final CompteRepository compteRepository;
    private final AdherentRepository adherentRepository;

    public CompteService() {
        this(new CompteRepository(), new AdherentRepository());
    }

    public CompteService(CompteRepository compteRepository, AdherentRepository adherentRepository) {
        this.compteRepository = compteRepository;
        this.adherentRepository = adherentRepository;
    }

    public int create(Compte compte) {
        requireId(compte.getAdherentId(), "L'adhérent du compte est obligatoire");
        requireText(compte.getTypeCompte(), "Le type de compte est obligatoire");
        adherentRepository.findById(compte.getAdherentId())
                .orElseThrow(() -> new IllegalArgumentException("L'adhérent indiqué n'existe pas"));
        if (compte.getSolde() == null) {
            compte.setSolde(BigDecimal.ZERO);
        }
        if (compte.getDateOuverture() == null) {
            compte.setDateOuverture(LocalDate.now());
        }
        if (compte.getActif() == null) {
            compte.setActif(Boolean.TRUE);
        }
        return compteRepository.create(compte);
    }

    public boolean update(Compte compte) {
        requireId(compte.getId(), "L'identifiant du compte est obligatoire");
        requireId(compte.getAdherentId(), "L'adhérent du compte est obligatoire");
        requireText(compte.getTypeCompte(), "Le type de compte est obligatoire");
        return compteRepository.update(compte);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant du compte est obligatoire");
        return compteRepository.delete(id);
    }

    public Optional<Compte> findById(Integer id) {
        return compteRepository.findById(id);
    }

    public List<Compte> findAll() {
        return compteRepository.findAll();
    }

    public Optional<Compte> findByAdherentId(Integer adherentId) {
        requireId(adherentId, "L'identifiant de l'adhérent est obligatoire");
        return compteRepository.findByAdherentId(adherentId);
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