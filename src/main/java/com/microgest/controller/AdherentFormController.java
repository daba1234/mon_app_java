package com.microgest.controller;

import com.microgest.model.Adherent;
import com.microgest.model.Agence;
import com.microgest.model.StatutAdherent;
import com.microgest.service.AdherentService;
import com.microgest.service.AgenceService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AdherentFormController {

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker dateNaissancePicker;

    @FXML
    private TextArea adresseArea;

    @FXML
    private DatePicker dateAdhesionPicker;

    @FXML
    private ComboBox<Agence> agenceComboBox;

    @FXML
    private ComboBox<StatutAdherent> statutComboBox;

    @FXML
    private Button saveButton;

    private final AdherentService adherentService = new AdherentService();
    private final AgenceService agenceService = new AgenceService();

    private Adherent adherent;
    private Runnable onSave;

    @FXML
    private void initialize() {
        statutComboBox.setItems(FXCollections.observableArrayList(StatutAdherent.values()));
        List<Agence> agences = agenceService.findAll();
        agenceComboBox.setItems(FXCollections.observableArrayList(agences));
        agenceComboBox.setCellFactory(listView -> new AgenceListCell());
        agenceComboBox.setButtonCell(new AgenceListCell());
        dateAdhesionPicker.setValue(LocalDate.now());
    }

    public void setAdherent(Adherent adherent) {
        this.adherent = adherent;
        boolean editMode = adherent != null && adherent.getId() != null;
        formTitleLabel.setText(editMode ? "Modifier un adhérent" : "Nouvel adhérent");
        if (!editMode) {
            statutComboBox.setValue(StatutAdherent.ACTIF);
            return;
        }

        nomField.setText(adherent.getNom());
        prenomField.setText(adherent.getPrenom());
        telephoneField.setText(adherent.getTelephone());
        emailField.setText(adherent.getEmail());
        dateNaissancePicker.setValue(adherent.getDateNaissance());
        adresseArea.setText(adherent.getAdresse());
        dateAdhesionPicker.setValue(adherent.getDateAdhesion());
        statutComboBox.setValue(adherent.getStatut());
        if (adherent.getAgenceId() != null) {
            agenceComboBox.getItems().stream()
                    .filter(agence -> adherent.getAgenceId().equals(agence.getId()))
                    .findFirst()
                    .ifPresent(agenceComboBox::setValue);
        }
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    @FXML
    private void onSaveClick() {
        try {
            boolean editMode = adherent != null && adherent.getId() != null;
            Adherent target = editMode ? adherent : new Adherent();
            target.setNom(clean(nomField.getText()));
            target.setPrenom(clean(prenomField.getText()));
            target.setTelephone(clean(telephoneField.getText()));
            target.setEmail(clean(emailField.getText()));
            target.setDateNaissance(dateNaissancePicker.getValue());
            target.setAdresse(clean(adresseArea.getText()));
            target.setDateAdhesion(dateAdhesionPicker.getValue());
            target.setStatut(statutComboBox.getValue());
            Agence agence = agenceComboBox.getValue();
            target.setAgenceId(agence == null ? null : agence.getId());

            if (editMode) {
                adherentService.update(target);
            } else {
                adherentService.create(target);
            }

            if (onSave != null) {
                onSave.run();
            }
            closeWindow();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showAlert(Alert.AlertType.ERROR, "Enregistrement impossible", ex.getMessage());
        }
    }

    @FXML
    private void onCancelClick() {
        closeWindow();
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class AgenceListCell extends javafx.scene.control.ListCell<Agence> {
        @Override
        protected void updateItem(Agence item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getNom() + " (#" + item.getId() + ")");
        }
    }
}
