package com.microgest.service;

import com.microgest.model.Utilisateur;
import com.microgest.repository.UtilisateurRepository;
import com.microgest.util.PasswordHasher;
import com.microgest.util.SecurityUtil;

import java.util.Optional;

public class AuthenticationService {

    private final UtilisateurRepository utilisateurRepository;

    public AuthenticationService() {
        this(new UtilisateurRepository());
    }

    public AuthenticationService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Optional<Utilisateur> authenticate(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return Optional.empty();
        }

        Optional<Utilisateur> utilisateur = utilisateurRepository.findByUsername(username.trim());
        if (utilisateur.isEmpty()) {
            return Optional.empty();
        }

        Utilisateur user = utilisateur.get();
        if (Boolean.FALSE.equals(user.getActif())) {
            return Optional.empty();
        }

        String storedHash = user.getPasswordHash();
        boolean ok = SecurityUtil.verifyPassword(rawPassword, storedHash);

        // Compatibilite temporaire: anciens comptes SHA-256 ou mot de passe en clair.
        if (!ok && storedHash != null && !storedHash.startsWith("$2")) {
            String sha256 = PasswordHasher.sha256(rawPassword);
            ok = storedHash.equals(sha256) || storedHash.equals(rawPassword);
            if (ok) {
                user.setPasswordHash(SecurityUtil.hashPassword(rawPassword));
                utilisateurRepository.update(user);
            }
        }

        return ok ? Optional.of(user) : Optional.empty();
    }
}