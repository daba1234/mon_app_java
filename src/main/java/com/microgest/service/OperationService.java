package com.microgest.service;

import com.microgest.model.Compte;
import com.microgest.model.Operation;
import com.microgest.model.TypeOperation;
import com.microgest.repository.CompteRepository;
import com.microgest.repository.OperationRepository;
import com.microgest.repository.UtilisateurRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OperationService {

    private final OperationRepository operationRepository;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public OperationService() {
        this(new OperationRepository(), new CompteRepository(), new UtilisateurRepository());
    }

    public OperationService(OperationRepository operationRepository, CompteRepository compteRepository, UtilisateurRepository utilisateurRepository) {
        this.operationRepository = operationRepository;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public int create(Operation operation) {
        requireId(operation.getCompteId(), "Le compte de l'opération est obligatoire");
        requireAmount(operation.getMontant(), "Le montant de l'opération est obligatoire");
        requireIdIfPresent(operation.getUtilisateurId(), "L'utilisateur de l'opération est invalide");

        Compte compte = compteRepository.findById(operation.getCompteId())
                .orElseThrow(() -> new IllegalArgumentException("Le compte indiqué n'existe pas"));
        if (operation.getUtilisateurId() != null) {
            utilisateurRepository.findById(operation.getUtilisateurId())
                    .orElseThrow(() -> new IllegalArgumentException("L'utilisateur indiqué n'existe pas"));
        }

        BigDecimal montant = operation.getMontant().setScale(2, RoundingMode.HALF_UP);
        BigDecimal solde = compte.getSolde() == null ? BigDecimal.ZERO : compte.getSolde();
        TypeOperation typeOperation = operation.getTypeOperation();
        if (typeOperation == null) {
            throw new IllegalArgumentException("Le type d'opération est obligatoire");
        }

        switch (typeOperation) {
            case DEPOT -> solde = solde.add(montant);
            case RETRAIT, PRET -> {
                if (solde.compareTo(montant) < 0) {
                    throw new IllegalArgumentException("Solde insuffisant pour cette opération");
                }
                solde = solde.subtract(montant);
            }
        }

        compte.setSolde(solde);
        compteRepository.update(compte);

        if (operation.getDateOperation() == null) {
            operation.setDateOperation(LocalDateTime.now());
        }
        return operationRepository.create(operation);
    }

    public boolean update(Operation operation) {
        requireId(operation.getId(), "L'identifiant de l'opération est obligatoire");
        requireId(operation.getCompteId(), "Le compte de l'opération est obligatoire");
        requireAmount(operation.getMontant(), "Le montant de l'opération est obligatoire");
        if (operation.getTypeOperation() == null) {
            throw new IllegalArgumentException("Le type d'opération est obligatoire");
        }
        return operationRepository.update(operation);
    }

    public boolean delete(Integer id) {
        requireId(id, "L'identifiant de l'opération est obligatoire");
        return operationRepository.delete(id);
    }

    public Optional<Operation> findById(Integer id) {
        return operationRepository.findById(id);
    }

    public List<Operation> findAll() {
        return operationRepository.findAll();
    }

    public List<Operation> findByCompteId(Integer compteId) {
        requireId(compteId, "L'identifiant du compte est obligatoire");
        return operationRepository.findByCompteId(compteId);
    }

    public List<Operation> findByUtilisateurId(Integer utilisateurId) {
        requireId(utilisateurId, "L'identifiant de l'utilisateur est obligatoire");
        return operationRepository.findByUtilisateurId(utilisateurId);
    }

    private void requireId(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireIdIfPresent(Integer value, String message) {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireAmount(BigDecimal value, String message) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}