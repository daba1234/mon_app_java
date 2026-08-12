/*package com.microgest.controllers;

import com.microgest.model.Adherent;
import com.microgest.model.Agence;
import com.microgest.model.StatutAdherent;
import com.microgest.repository.AgenceRepository;
import com.microgest.service.AdherentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AdherentFormController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextField adresseField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private DatePicker dateAdhesionPicker;
    @FXML private ComboBox<StatutAdherent> statutCombo;
    @FXML private ComboBox<Agence> agenceCombo;
    @FXML private Label titleLabel;

    private final AdherentService adherentService = new AdherentService();
    private final AgenceRepository agenceRepository = new AgenceRepository();
    private Consumer<Adherent> onSaved;
    private Adherent currentAdherent;

    @FXML
    private void initialize() {
        statutCombo.setItems(FXCollections.observableArrayList(StatutAdherent.ACTIF, StatutAdherent.SUSPENDU, StatutAdherent.FERME));
        statutCombo.getSelectionModel().select(StatutAdherent.ACTIF);

        List<Agence> agences = agenceRepository.findAll();
        agenceCombo.setItems(FXCollections.observableArrayList(agences));
        agenceCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Agence item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getId() + " - " + item.getNom());
            }
        });
        agenceCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Agence item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getId() + " - " + item.getNom());
            }
        });

        if (!agences.isEmpty()) {
            agenceCombo.getSelectionModel().selectFirst();
        }
        dateAdhesionPicker.setValue(LocalDate.now());
    }

    public void setOnSaved(Consumer<Adherent> onSaved) {
        this.onSaved = onSaved;
    }

    public void setAdherent(Adherent adherent) {
        this.currentAdherent = adherent;
        if (adherent == null) {
            titleLabel.setText("Ajouter un adhérent");
            return;
        }

        titleLabel.setText("Modifier un adhérent");
        nomField.setText(adherent.getNom());
        prenomField.setText(adherent.getPrenom());
        telephoneField.setText(adherent.getTelephone());
        emailField.setText(adherent.getEmail());
        adresseField.setText(adherent.getAdresse());
        dateNaissancePicker.setValue(adherent.getDateNaissance());
        dateAdhesionPicker.setValue(adherent.getDateAdhesion());
        if (adherent.getStatut() != null) {
            statutCombo.getSelectionModel().select(adherent.getStatut());
        }
        if (adherent.getAgenceId() != null) {
            agenceCombo.getItems().stream()
                    .filter(agence -> adherent.getAgenceId().equals(agence.getId()))
                    .findFirst()
                    .ifPresent(agence -> agenceCombo.getSelectionModel().select(agence));
        }
    }

    @FXML
    private void onSave() {
        try {
            Adherent adherent = currentAdherent == null ? new Adherent() : currentAdherent;
            adherent.setNom(required(nomField.getText(), "Le nom est obligatoire"));
            adherent.setPrenom(required(prenomField.getText(), "Le prénom est obligatoire"));
            adherent.setTelephone(emptyToNull(telephoneField.getText()));
            adherent.setEmail(emptyToNull(emailField.getText()));
            adherent.setAdresse(emptyToNull(adresseField.getText()));
            adherent.setDateNaissance(dateNaissancePicker.getValue());
            adherent.setDateAdhesion(dateAdhesionPicker.getValue());
            adherent.setStatut(statutCombo.getValue());
            if (agenceCombo.getValue() == null) {
                throw new IllegalArgumentException("Veuillez sélectionner une agence");
            }
            adherent.setAgenceId(agenceCombo.getValue().getId());

            if (adherent.getId() == null) {
                adherentService.create(adherent);
            } else {
                adherentService.update(adherent);
            }

            if (onSaved != null) {
                onSaved.accept(adherent);
            }
            closeWindow();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private String required(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String emptyToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}*/