package com.microgest.controller;

import com.microgest.model.Adherent;
import com.microgest.model.PageResult;
import com.microgest.model.StatutAdherent;
import com.microgest.service.AdherentService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdherentListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statutFilterComboBox;

    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private Button firstButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button lastButton;

    @FXML
    private TableView<Adherent> adherentTable;

    @FXML
    private TableColumn<Adherent, Integer> idColumn;

    @FXML
    private TableColumn<Adherent, String> nomColumn;

    @FXML
    private TableColumn<Adherent, String> prenomColumn;

    @FXML
    private TableColumn<Adherent, String> telephoneColumn;

    @FXML
    private TableColumn<Adherent, String> emailColumn;

    @FXML
    private TableColumn<Adherent, StatutAdherent> statutColumn;

    private final AdherentService adherentService = new AdherentService();

    private int currentPage = 0;
    private Runnable onDataChanged;

    @FXML
    private void initialize() {
        statutFilterComboBox.setItems(FXCollections.observableArrayList("Tous", "ACTIF", "SUSPENDU", "FERME"));
        statutFilterComboBox.setValue("Tous");

        pageSizeComboBox.setItems(FXCollections.observableArrayList(5, 10, 20, 50));
        pageSizeComboBox.setValue(10);
        pageSizeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            currentPage = 0;
            loadPage();
        });

        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        prenomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPrenom()));
        telephoneColumn.setCellValueFactory(data -> new SimpleStringProperty(emptyToDash(data.getValue().getTelephone())));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(emptyToDash(data.getValue().getEmail())));
        statutColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStatut()));

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            currentPage = 0;
            loadPage();
        });

        loadPage();
    }

    public void setOnDataChanged(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
    }

    @FXML
    private void onApplyFiltersClick() {
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onResetFiltersClick() {
        searchField.clear();
        statutFilterComboBox.setValue("Tous");
        pageSizeComboBox.setValue(10);
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onAddClick() {
        openForm(null);
    }

    @FXML
    private void onEditClick() {
        Adherent selected = adherentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un adhérent à modifier.");
            return;
        }
        openForm(selected);
    }

    @FXML
    private void onDeleteClick() {
        Adherent selected = adherentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", "Veuillez sélectionner un adhérent à supprimer.");
            return;
        }
        try {
            boolean deleted = adherentService.delete(selected.getId());
            if (deleted) {
                loadPage();
                notifyDataChanged();
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showAlert(Alert.AlertType.ERROR, "Suppression impossible", ex.getMessage());
        }
    }

    @FXML
    private void onFirstClick() {
        currentPage = 0;
        loadPage();
    }

    @FXML
    private void onPreviousClick() {
        if (currentPage > 0) {
            currentPage--;
            loadPage();
        }
    }

    @FXML
    private void onNextClick() {
        currentPage++;
        loadPage();
    }

    @FXML
    private void onLastClick() {
        PageResult<Adherent> page = adherentService.findPage(searchField.getText(), selectedStatut(), currentPage, pageSizeComboBox.getValue());
        currentPage = Math.max(page.getTotalPages() - 1, 0);
        loadPage();
    }

    private void loadPage() {
        try {
            PageResult<Adherent> page = adherentService.findPage(searchField.getText(), selectedStatut(), currentPage, pageSizeComboBox.getValue());
            currentPage = page.getPageNumber();
            adherentTable.setItems(FXCollections.observableArrayList(page.getItems()));
            pageInfoLabel.setText("Page " + (page.getPageNumber() + 1) + " / " + page.getTotalPages());
            firstButton.setDisable(page.isFirstPage());
            previousButton.setDisable(page.isFirstPage());
            nextButton.setDisable(page.isLastPage());
            lastButton.setDisable(page.isLastPage());
        } catch (IllegalStateException ex) {
            showAlert(Alert.AlertType.ERROR, "Chargement impossible", ex.getMessage());
        }
    }

    private void openForm(Adherent adherent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adherent-form.fxml"));
            Parent root = loader.load();
            AdherentFormController controller = loader.getController();
            controller.setAdherent(adherent == null ? null : copyOf(adherent));
            controller.setOnSave(() -> {
                loadPage();
                notifyDataChanged();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(adherent == null ? "Nouvel adhérent" : "Modifier l'adhérent");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Fenêtre indisponible", "Impossible d'ouvrir le formulaire d'adhérent.");
        }
    }

    private Adherent copyOf(Adherent source) {
        Adherent copy = new Adherent();
        copy.setId(source.getId());
        copy.setNom(source.getNom());
        copy.setPrenom(source.getPrenom());
        copy.setTelephone(source.getTelephone());
        copy.setEmail(source.getEmail());
        copy.setDateNaissance(source.getDateNaissance());
        copy.setAdresse(source.getAdresse());
        copy.setDateAdhesion(source.getDateAdhesion());
        copy.setAgenceId(source.getAgenceId());
        copy.setStatut(source.getStatut());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

    private StatutAdherent selectedStatut() {
        String value = statutFilterComboBox.getValue();
        if (value == null || "Tous".equalsIgnoreCase(value)) {
            return null;
        }
        return StatutAdherent.valueOf(value);
    }

    private String emptyToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void notifyDataChanged() {
        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
