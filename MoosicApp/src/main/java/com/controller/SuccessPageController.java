package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;


public class SuccessPageController {

    @FXML
    private Button returnButton;

    @FXML
    private void handleReturntoHome(ActionEvent event) {
        System.out.println("Returning to Home...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

            try {
                String cssUrl = getClass().getResource("/css/homepage.css").toExternalForm();
                scene.getStylesheets().add(cssUrl);
                System.out.println("✅ Loaded homepage CSS");
            } catch (Exception e) {
                System.out.println("⚠️ Homepage CSS not found - " + e.getMessage());
            }

            stage.setScene(scene);
            stage.setTitle("Moosic - Home");

            ensureMaximizedWindow(stage);

            stage.show();
            System.out.println("✅ Successfully navigated to Home page (Full Screen).");

        } catch (IOException e) {
            System.err.println("❌ Failed to load homepage.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

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