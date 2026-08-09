package com.microgest.service;

import com.microgest.model.Role;
import com.microgest.model.Utilisateur;
import com.microgest.repository.AgenceRepository;
import com.microgest.repository.UtilisateurRepository;
import com.microgest.util.EmailValidator;
import com.microgest.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final AgenceRepository agenceRepository;

    public UtilisateurService() {
        this(new UtilisateurRepository(), new AgenceRepository());
    }

    public UtilisateurService(UtilisateurRepository utilisateurRepository, AgenceRepository agenceRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.agenceRepository = agenceRepository;
    }

    public int create(Utilisateur utilisateur) {
        validateUtilisateur(utilisateur, false);
        return utilisateurRepository.create(applyDefaults(utilisateur));
    }

    public int createWithRawPassword(Utilisateur utilisateur, String rawPassword) {
        utilisateur.setPasswordHash(SecurityUtil.hashPassword(rawPassword));
        return create(utilisateur);
    }

    public boolean update(Utilisateur utilisateur) {
        validateUtilisateur(utilisateur, true);
        return utilisateurRepository.update(utilisateur);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant de l'utilisateur est obligatoire");
        return utilisateurRepository.delete(id);
    }

    public Optional<Utilisateur> findById(Integer id) {
        return utilisateurRepository.findById(id);
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> findByUsername(String username) {
        return utilisateurRepository.findByUsername(username);
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return utilisateurRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    private Utilisateur applyDefaults(Utilisateur utilisateur) {
        if (utilisateur.getDateCreation() == null) {
            utilisateur.setDateCreation(LocalDateTime.now());
        }
        if (utilisateur.getActif() == null) {
            utilisateur.setActif(Boolean.TRUE);
        }
        return utilisateur;
    }

    private void validateUtilisateur(Utilisateur utilisateur, boolean requireId) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire");
        }
        if (requireId) {
            requireId(utilisateur.getId(), "L'identifiant de l'utilisateur est obligatoire");
        }
        requireText(utilisateur.getUsername(), "Le nom d'utilisateur est obligatoire");
        requireText(utilisateur.getPasswordHash(), "Le mot de passe est obligatoire");
        requireText(utilisateur.getEmail(), "L'email est obligatoire");
        if (!EmailValidator.isValid(utilisateur.getEmail())) {
            throw new IllegalArgumentException("L'adresse email est invalide");
        }
        if (utilisateur.getRole() == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }
        if (utilisateur.getAgenceId() != null) {
            agenceRepository.findById(utilisateur.getAgenceId())
                    .orElseThrow(() -> new IllegalArgumentException("L'agence indiquée n'existe pas"));
        }
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