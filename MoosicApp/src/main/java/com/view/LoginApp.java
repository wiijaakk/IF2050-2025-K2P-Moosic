package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;

public class LoginApp extends Application {

    private static final double INITIAL_WINDOW_WIDTH = 800;
    private static final double INITIAL_WINDOW_HEIGHT = 600;
    private static final double LOGIN_FORM_DEFAULT_WIDTH = 350;
    private static final double LOGIN_FORM_DEFAULT_HEIGHT = 450;
    private static final double RIGHT_MARGIN = 50.0;
    private static final double TOP_BOTTOM_MARGIN = 50.0;

    private static final double LOGO_DEFAULT_FIT_WIDTH = 250.0;
    private static final double LOGO_MAXIMIZED_SCALE_FACTOR = 1.2;

    private VBox loginFormBox;
    private ImageView logoImageView;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Moosic Login");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();

        AnchorPane rootAnchorPane = (AnchorPane) root;
        ImageView backgroundImageView = (ImageView) rootAnchorPane.lookup("#backgroundImageView");
        loginFormBox = (VBox) rootAnchorPane.lookup("#login-form-box");
        logoImageView = (ImageView) rootAnchorPane.lookup("#logoImageView");

        if (backgroundImageView != null) {
            backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
            backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
            backgroundImageView.setPreserveRatio(false);
        } else {
            System.err.println("Error: backgroundImageView tidak ditemukan di FXML! Periksa fx:id.");
        }

        if (loginFormBox != null && logoImageView != null) {
            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> updateLoginLayout());
            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> updateLoginLayout());
            primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> updateLoginLayout());

            updateLoginLayout();

        } else {
            System.err.println("Error: loginFormBox atau logoImageView tidak ditemukan di FXML! Periksa fx:id.");
        }

        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        URL cssUrl = getClass().getResource("/css/loginstyle.css");
        if (cssUrl == null) {
            System.err.println("Error: loginstyle.css tidak ditemukan! Pastikan ada di direktori yang benar (misalnya, /src/main/resources/css/loginstyle.css).");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateLoginLayout() {
        if (primaryStage == null || loginFormBox == null || logoImageView == null) {
            return;
        }

        double currentStageWidth = primaryStage.getWidth();
        double currentStageHeight = primaryStage.getHeight();

        double loginFormAspectRatio = LOGIN_FORM_DEFAULT_WIDTH / LOGIN_FORM_DEFAULT_HEIGHT;

        double targetLoginFormWidth;
        double targetLoginFormHeight;
        double targetLogoFitWidth;

        if (primaryStage.isMaximized()) {
            double availableHeightForForm = currentStageHeight - (2 * TOP_BOTTOM_MARGIN);
            double calculatedWidthBasedOnHeight = availableHeightForForm * loginFormAspectRatio;

            if (calculatedWidthBasedOnHeight > loginFormBox.getMaxWidth()) {
                targetLoginFormWidth = loginFormBox.getMaxWidth();
                targetLoginFormHeight = targetLoginFormWidth / loginFormAspectRatio;
            } else {
                targetLoginFormWidth = calculatedWidthBasedOnHeight;
                targetLoginFormHeight = availableHeightForForm;
            }

            if (targetLoginFormHeight > loginFormBox.getMaxHeight()) {
                targetLoginFormHeight = loginFormBox.getMaxHeight();
                targetLoginFormWidth = targetLoginFormHeight * loginFormAspectRatio;
            }

            targetLogoFitWidth = targetLoginFormWidth * (LOGO_DEFAULT_FIT_WIDTH / LOGIN_FORM_DEFAULT_WIDTH) * LOGO_MAXIMIZED_SCALE_FACTOR;

        } else {
            double availableWidthForForm = currentStageWidth - (2 * RIGHT_MARGIN);
            double availableHeightForForm = currentStageHeight - (2 * TOP_BOTTOM_MARGIN);

            double potentialWidthBasedOnHeight = availableHeightForForm * loginFormAspectRatio;
            double potentialHeightBasedOnWidth = availableWidthForForm / loginFormAspectRatio;

            if (potentialWidthBasedOnHeight <= availableWidthForForm) {
                targetLoginFormWidth = potentialWidthBasedOnHeight;
                targetLoginFormHeight = availableHeightForForm;
            } else {
                targetLoginFormWidth = availableWidthForForm;
                targetLoginFormHeight = potentialHeightBasedOnWidth;
            }
            targetLoginFormWidth = Math.min(targetLoginFormWidth, LOGIN_FORM_DEFAULT_WIDTH);
            targetLoginFormHeight = Math.min(targetLoginFormHeight, LOGIN_FORM_DEFAULT_HEIGHT);

            targetLogoFitWidth = LOGO_DEFAULT_FIT_WIDTH;
        }

        double leftAnchor = currentStageWidth - targetLoginFormWidth - RIGHT_MARGIN;
        AnchorPane.setLeftAnchor(loginFormBox, leftAnchor);
        loginFormBox.setPrefWidth(targetLoginFormWidth);
        loginFormBox.setPrefHeight(targetLoginFormHeight);

        logoImageView.setFitWidth(targetLogoFitWidth);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
