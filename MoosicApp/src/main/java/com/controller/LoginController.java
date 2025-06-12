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
            
            // Navigate to homepage with full screen
            navigateToPage("/fxml/homepage.fxml", "MoosicApp Dashboard", "/css/homepage.css");
            
        } else {
            errorLabel.setText(loginResult);
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleRegisterLinkAction() {
        System.out.println("Register clicked!");
        
        // Navigate to register page with full screen
        navigateToPage("/fxml/register.fxml", "Moosic - Register Account", "/css/register.css");
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
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            
            // Save current window state
            boolean wasMaximized = stage.isMaximized();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Create scene with appropriate size
            Scene scene;
            if (wasMaximized) {
                scene = new Scene(root, stage.getWidth(), stage.getHeight());
            } else {
                scene = new Scene(root, currentWidth, currentHeight);
                // Force maximize for navigation
                wasMaximized = true;
            }

            // Load CSS if provided
            if (cssPath != null) {
                try {
                    URL cssUrl = getClass().getResource(cssPath);
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println("✅ Loaded CSS: " + cssPath);
                    } else {
                        System.out.println("⚠️ CSS not found: " + cssPath);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Error loading CSS: " + cssPath + " - " + e.getMessage());
                }
            }

            // Set scene
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Force maximize window with enhanced method
            ensureMaximizedWindow(stage);
            
            stage.show();
            System.out.println("✅ Successfully navigated to " + title + " (Full Screen)");

        } catch (IOException e) {
            System.err.println("❌ IO Error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Could not navigate to " + title + ". File not found: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                     "An unexpected error occurred: " + e.getMessage());
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