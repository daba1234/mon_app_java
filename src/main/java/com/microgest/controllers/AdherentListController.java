/*package com.microgest.controllers;

import com.microgest.model.Adherent;
import com.microgest.model.StatutAdherent;
import com.microgest.service.AdherentService;
import com.microgest.service.DashboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class AdherentListController {

    @FXML private TableView<Adherent> adherentTable;
    @FXML private TableColumn<Adherent, Integer> idColumn;
    @FXML private TableColumn<Adherent, String> nomColumn;
    @FXML private TableColumn<Adherent, String> prenomColumn;
    @FXML private TableColumn<Adherent, String> telColumn;
    @FXML private TableColumn<Adherent, String> emailColumn;
    @FXML private TableColumn<Adherent, String> statutColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<StatutAdherent> statutFilterCombo;
    @FXML private ComboBox<Integer> pageSizeCombo;
    @FXML private Label pageLabel;
    @FXML private Button firstButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button lastButton;

    @FXML private Label totalAdherentsLabel;
    @FXML private Label actifsLabel;
    @FXML private Label operationsMoisLabel;
    @FXML private Label totalEpargneLabel;
    @FXML private PieChart statutPieChart;
    @FXML private BarChart<String, Number> operationsBarChart;

    private final AdherentService adherentService = new AdherentService();
    private final DashboardService dashboardService = new DashboardService();
    private final ObservableList<Adherent> adherents = FXCollections.observableArrayList();

    private int pageIndex = 0;
    private int pageSize = 10;
    private long totalItems = 0;
    private String searchText = "";
    private StatutAdherent statutFilter = null;

    @FXML
    private void initialize() {
        configureTable();
        configureFilters();
        configurePagination();
        refreshAll();
    }

    private void configureTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        telColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        statutColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getStatut() == null ? "" : cell.getValue().getStatut().name()));

        adherentTable.setItems(adherents);
        adherentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        adherentTable.setRowFactory(table -> {
            TableRow<Adherent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditForm(row.getItem());
                }
            });
            return row;
        });
    }

    private void configureFilters() {
        statutFilterCombo.setItems(FXCollections.observableArrayList((StatutAdherent[]) null));
        statutFilterCombo.setItems(FXCollections.observableArrayList(StatutAdherent.ACTIF, StatutAdherent.SUSPENDU, StatutAdherent.FERME));
        statutFilterCombo.getItems().add(0, null);
        statutFilterCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(StatutAdherent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tous" : item.name());
            }
        });
        statutFilterCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(StatutAdherent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tous" : item.name());
            }
        });
        statutFilterCombo.getSelectionModel().select(null);

        pageSizeCombo.setItems(FXCollections.observableArrayList(5, 10, 20, 50));
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(pageSize));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue == null ? "" : newValue.trim();
            pageIndex = 0;
            loadPage();
        });
    }

    private void configurePagination() {
        pageSizeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue > 0) {
                pageSize = newValue;
                pageIndex = 0;
                loadPage();
            }
        });
    }

    @FXML
    private void onFirstPage() {
        if (pageIndex != 0) {
            pageIndex = 0;
            loadPage();
        }
    }

    @FXML
    private void onPreviousPage() {
        if (pageIndex > 0) {
            pageIndex--;
            loadPage();
        }
    }

    @FXML
    private void onNextPage() {
        if (pageIndex + 1 < totalPages()) {
            pageIndex++;
            loadPage();
        }
    }

    @FXML
    private void onLastPage() {
        int pages = totalPages();
        if (pages > 0 && pageIndex != pages - 1) {
            pageIndex = pages - 1;
            loadPage();
        }
    }

    @FXML
    private void onApplyFilters() {
        statutFilter = statutFilterCombo.getValue();
        pageIndex = 0;
        loadPage();
    }

    @FXML
    private void onResetFilters() {
        searchField.clear();
        statutFilterCombo.getSelectionModel().select(null);
        statutFilter = null;
        searchText = "";
        pageIndex = 0;
        loadPage();
    }

    @FXML
    private void onRefresh() {
        refreshAll();
    }

    @FXML
    private void onAdd() {
        openForm(null);
    }

    @FXML
    private void onEdit() {
        Adherent selected = adherentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openForm(selected);
        }
    }

    @FXML
    private void onDelete() {
        Adherent selected = adherentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer l'adhérent sélectionné ?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                adherentService.delete(selected.getId());
                loadPage();
                refreshDashboard();
            }
        });
    }

    private void refreshAll() {
        pageIndex = 0;
        loadPage();
        refreshDashboard();
    }

    private void loadPage() {
        totalItems = adherentService.countFiltered(searchText, statutFilter);
        adherents.setAll(adherentService.findPage(pageIndex, pageSize, searchText, statutFilter));
        pageLabel.setText("Page " + currentPageNumber() + " / " + Math.max(totalPages(), 1));

        boolean first = pageIndex <= 0;
        boolean last = pageIndex + 1 >= totalPages();
        firstButton.setDisable(first);
        previousButton.setDisable(first);
        nextButton.setDisable(last);
        lastButton.setDisable(last);
    }

    private int currentPageNumber() {
        return totalPages() == 0 ? 0 : pageIndex + 1;
    }

    private int totalPages() {
        if (totalItems <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    private void openForm(Adherent adherent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/adherent-form.fxml"));
            Parent root = loader.load();
            AdherentFormController controller = loader.getController();
            controller.setAdherent(adherent);
            controller.setOnSaved((adherent1) -> {
                loadPage();
                refreshDashboard();
            });

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(adherent == null ? "Ajouter un adhérent" : "Modifier un adhérent");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.centerOnScreen();
            dialog.showAndWait();
        } catch (IOException ex) {
            throw new IllegalStateException("Impossible d'ouvrir le formulaire adhérent", ex);
        }
    }

    private void openEditForm(Adherent adherent) {
        openForm(adherent);
    }

    private void refreshDashboard() {
        totalAdherentsLabel.setText(String.valueOf(dashboardService.totalAdherents()));
        actifsLabel.setText(String.valueOf(dashboardService.adherentsActifs()));
        operationsMoisLabel.setText(String.valueOf(dashboardService.operationsDuMois()));
        BigDecimal totalEpargne = dashboardService.totalEpargne();
        totalEpargneLabel.setText(totalEpargne == null ? "0" : totalEpargne.toPlainString());

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : dashboardService.adherentsByStatus().entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        statutPieChart.setData(pieData);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Opérations");
        for (Map.Entry<String, Long> entry : dashboardService.operationsByMonth().entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        operationsBarChart.getData().setAll(series);
    }
}*/