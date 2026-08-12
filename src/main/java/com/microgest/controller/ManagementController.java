package com.microgest.controller;

import com.microgest.model.DashboardStats;
import com.microgest.model.MonthlyOperationCount;
import com.microgest.model.StatusCount;
import com.microgest.service.AdherentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class ManagementController {

    @FXML
    private Label totalAdherentsLabel;

    @FXML
    private Label activeAdherentsLabel;

    @FXML
    private Label operationsMonthLabel;

    @FXML
    private Label totalSavingsLabel;

    @FXML
    private PieChart statutPieChart;

    @FXML
    private BarChart<String, Number> operationsBarChart;

    @FXML
    private AdherentListController adherentListViewController;

    private final AdherentService adherentService = new AdherentService();
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    @FXML
    private void initialize() {
        if (adherentListViewController != null) {
            adherentListViewController.setOnDataChanged(this::refreshDashboard);
        }
        refreshDashboard();
    }

    @FXML
    private void onRefreshDashboardClick() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            DashboardStats stats = adherentService.loadDashboardStats();
            totalAdherentsLabel.setText(String.valueOf(stats.getTotalAdherents()));
            activeAdherentsLabel.setText(String.valueOf(stats.getActiveAdherents()));
            operationsMonthLabel.setText(String.valueOf(stats.getOperationsThisMonth()));
            totalSavingsLabel.setText(currencyFormatter.format(defaultAmount(stats.getTotalSavings())));

            statutPieChart.setData(FXCollections.observableArrayList(
                    stats.getStatusCounts().stream()
                            .map(this::toPieData)
                            .toList()
            ));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Opérations");
            for (MonthlyOperationCount item : stats.getMonthlyOperationCounts()) {
                series.getData().add(new XYChart.Data<>(item.getMonthLabel(), item.getTotal()));
            }
            operationsBarChart.getData().setAll(series);
        } catch (IllegalStateException ex) {
            showAlert("Chargement impossible", ex.getMessage());
        }
    }

    private PieChart.Data toPieData(StatusCount statusCount) {
        String label = statusCount.getStatut() == null ? "Non défini" : statusCount.getStatut().name();
        return new PieChart.Data(label + " (" + statusCount.getTotal() + ")", statusCount.getTotal());
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
