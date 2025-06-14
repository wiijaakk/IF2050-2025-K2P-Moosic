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
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URL;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;


public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private BorderPane mainContainer;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();

        passwordField.setOnAction(e -> handleLoginButtonAction());

        usernameField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String loginResult = userDAO.login(username, password);

        if (loginResult.contains("Berhasil")) {
            System.out.println(loginResult + "!");
            errorLabel.setVisible(false);
            
            // Navigate to homepage with full screen
            navigateToPage("/fxml/homepage.fxml", "MoosicApp Dashboard", "/css/homepage.css");
            
        } else {
            errorLabel.setText(loginResult);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegisterLinkAction() {
        // 1. Informasi yang tadinya parameter, sekarang kita definisikan di sini
        String fxmlPath = "/fxml/register.fxml";
        String title = "Register Page";
        String cssPath = "/css/register.css";

        // 2. Sisa logika di dalamnya tetap sama persis
        try {
            System.out.println("🔍 Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homePage = loader.load();

            // Clear main container and set page
            mainContainer.setTop(null);
            mainContainer.setBottom(null);
            mainContainer.setCenter(null);
            mainContainer.setCenter(homePage);

            // Load CSS if provided
            if (cssPath != null && !cssPath.isEmpty()) {
                try {
                    String cssUrl = getClass().getResource(cssPath).toExternalForm();
                    homePage.getStylesheets().add(cssUrl);
                    System.out.println("Loading CSS from: " + cssUrl);
                } catch (Exception e) {
                    System.out.println("CSS not found, using default styling");
                }
            }

            System.out.println("Switched to " + title + " (inline)");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open page: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                "An unexpected error occurred while navigating: " + e.getMessage());
        }
    }

    /**
     * Generic navigation method with FULL SCREEN enforcement
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param cssPath Path to CSS file (can be null)
     */
    private void navigateToPage(String fxmlPath, String title, String cssPath) {
        try {
            System.out.println("🔍 Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent homePage = loader.load();

            // Clear main container and set page
            mainContainer.setTop(null);
            mainContainer.setBottom(null);
            mainContainer.setCenter(null);
            mainContainer.setCenter(homePage);

            // Load CSS if provided
            if (cssPath != null && !cssPath.isEmpty()) {
                try {
                    String cssUrl = getClass().getResource(cssPath).toExternalForm();
                    homePage.getStylesheets().add(cssUrl);
                    System.out.println("Loading CSS from: " + cssUrl);
                } catch (Exception e) {
                    System.out.println("CSS not found, using default styling");
                }
            }

            System.out.println("Switched to " + title + " (inline)");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open page: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                "An unexpected error occurred while navigating: " + e.getMessage());
        }
    }

    /**
     * Enhanced method untuk memastikan window full screen
     * @param stage The JavaFX Stage to maximize
     */
    private void ensureMaximizedWindow(Stage stage) {
        try {
            // Method 1: Set maximized immediately
            stage.setMaximized(false); // Reset first
            stage.setMaximized(true);  // Then maximize
            
            // Method 2: Use Platform.runLater for delayed execution
            Platform.runLater(() -> {
                if (!stage.isMaximized()) {
                    stage.setMaximized(true);
                }
                stage.toFront();
                stage.requestFocus();
            });
            
            // Method 3: Use Timeline for multiple attempts (aggressive approach)
            Timeline maxChecker = new Timeline();
            maxChecker.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(50), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Maximized check 1 - Applied");
                    }
                }),
                new KeyFrame(Duration.millis(150), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Maximized check 2 - Applied");
                    }
                }),
                new KeyFrame(Duration.millis(300), e -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                        System.out.println("🔄 Final maximized attempt completed");
                    }
                    System.out.println("✅ Final window state - Maximized: " + stage.isMaximized());
                })
            );
            maxChecker.play();
            
        } catch (Exception e) {
            System.err.println("⚠️ Error ensuring maximized window: " + e.getMessage());
            e.printStackTrace();
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