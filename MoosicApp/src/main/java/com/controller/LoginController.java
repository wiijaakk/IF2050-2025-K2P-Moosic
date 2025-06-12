package com.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.database.UserDAO;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String loginResult = userDAO.login(username, password);

        if (loginResult.contains("Berhasil")) {
            System.out.println(loginResult + "!");
            errorLabel.setVisible(false);
            // TODO: Pindah ke halaman selanjutnya setelah login berhasil (sesuai diskusi sebelumnya)
        } else {
            errorLabel.setText(loginResult);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegisterLinkAction() {
        System.out.println("Register clicked!");
        // TODO: Pindah ke halaman registrasi (sesuai diskusi sebelumnya)
    }
}