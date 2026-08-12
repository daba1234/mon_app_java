package com.microgest.controller;

import com.microgest.model.Utilisateur;
import com.microgest.service.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final AuthenticationService authenticationService = new AuthenticationService();

    @FXML
    private void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Optional<Utilisateur> authenticated = authenticationService.authenticate(username, password);
        if (authenticated.isPresent()) {
            openManagementScreen(event, authenticated.get());
        } else {
            showError("Échec de connexion", "Identifiants invalides ou utilisateur inactif.");
        }
    }

    private void openManagementScreen(ActionEvent event, Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/management.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("MicroGest - Gestion des adhérents - " + utilisateur.getUsername());
            stage.setScene(new Scene(root, 1280, 820));
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException ex) {
            showError("Chargement impossible", "La page de gestion des adhérents n'a pas pu être ouverte.");
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
