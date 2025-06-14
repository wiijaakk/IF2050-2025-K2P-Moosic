package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;     // Import Node
import javafx.scene.Parent;   // Import Parent
import javafx.scene.Scene;    // Import Scene
import javafx.scene.control.Button;
import javafx.stage.Stage;    // Import Stage
import java.io.IOException;   // Import IOException
import javafx.animation.Timeline; // For ensuring maximized window
import javafx.animation.KeyFrame;   // For ensuring maximized window
import javafx.util.Duration;    // For ensuring maximized window


public class SuccessPageController {

    @FXML
    private Button returnButton;

    @FXML
    private void handleReturntoHome(ActionEvent event) {
        System.out.println("Returning to Home...");
        try {
            // Load the homepage.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepage.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Create a new scene with the homepage.fxml content
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight()); // Maintain current window size

            // Load homepage.css if it exists
            try {
                String cssUrl = getClass().getResource("/css/homepage.css").toExternalForm();
                scene.getStylesheets().add(cssUrl);
                System.out.println("✅ Loaded homepage CSS");
            } catch (Exception e) {
                System.out.println("⚠️ Homepage CSS not found - " + e.getMessage());
            }

            // Set the new scene to the stage
            stage.setScene(scene);
            stage.setTitle("Moosic - Home"); // Set the title for the home page

            // Ensure the window is maximized (optional, based on your app's behavior)
            ensureMaximizedWindow(stage);

            stage.show();
            System.out.println("✅ Successfully navigated to Home page (Full Screen).");

        } catch (IOException e) {
            System.err.println("❌ Failed to load homepage.fxml: " + e.getMessage());
            e.printStackTrace();
            // You might want to show an alert to the user here
            // showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load the home page.");
        }
    }

    // Helper method to ensure the window is maximized, copied from CheckoutController
    private void ensureMaximizedWindow(Stage stage) {
        try {
            stage.setMaximized(false);
            stage.setMaximized(true);

            javafx.application.Platform.runLater(() -> {
                if (!stage.isMaximized()) {
                    stage.setMaximized(true);
                }
                stage.toFront();
                stage.requestFocus();
            });

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
}