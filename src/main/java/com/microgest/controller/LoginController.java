package com.microgest.controller;

import com.microgest.model.Utilisateur;
import com.microgest.service.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            showInfo("Connexion réussie", "Bienvenue " + authenticated.get().getUsername() + " !");
            passwordField.clear();
        } else {
            showError("Échec de connexion", "Identifiants invalides ou utilisateur inactif.");
        }
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}