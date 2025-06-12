// MoosicApp/src/main/java/com/controller/LoginController.java

package com.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.database.UserDAO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;

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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepage.fxml"));
                Parent dashboardRoot = loader.load();

                // Dapatkan Stage yang sedang aktif
                Stage stage = (Stage) usernameField.getScene().getWindow();

                // Simpan status maximized
                boolean wasMaximized = stage.isMaximized();
                
                Scene scene = new Scene(dashboardRoot);
                // Tambahkan stylesheet yang relevan untuk Dashboard
                URL homepageCssUrl = getClass().getResource("/css/homepage.css");
                if (homepageCssUrl != null) {
                    scene.getStylesheets().add(homepageCssUrl.toExternalForm());
                } else {
                    System.err.println("Error: homepage.css not found for Dashboard.");
                }
                // Anda mungkin perlu menambahkan CSS lain yang umum untuk dashboard di sini juga

                stage.setScene(scene);
                stage.setTitle("MoosicApp Dashboard");

                // Setel ulang status maximized jika sebelumnya dimaksimalkan
                if (wasMaximized) {
                    stage.setMaximized(true);
                }
                // Jika Anda ingin selalu memulai dalam maximized, cukup set stage.setMaximized(true);

                stage.show();

                System.out.println("Navigated to Dashboard after successful login. Maximize status: " + stage.isMaximized());
            } catch (IOException e) {
                System.err.println("Failed to load Dashboard.fxml: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the dashboard page.");
            }
        } else {
            errorLabel.setText(loginResult);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegisterLinkAction() {
        System.out.println("Register clicked!");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent registerRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Simpan status maximized
            boolean wasMaximized = stage.isMaximized();

            Scene scene = new Scene(registerRoot);
            URL cssUrl = getClass().getResource("/css/register.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Error: register.css not found for registration page.");
            }
            
            stage.setScene(scene);
            stage.setTitle("Moosic - Register Account");
            
            // Setel ulang status maximized
            if (wasMaximized) {
                stage.setMaximized(true);
            }

            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load register.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the registration page.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}