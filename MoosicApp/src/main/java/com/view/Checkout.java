package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

public class Checkout extends Application { 

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Checkout.fxml"));
       
        Parent root = loader.load();

        
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root, 950, 680);

        primaryStage.setTitle("Moosic - Checkout");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(550);
        primaryStage.centerOnScreen();

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F11:
                    if (primaryStage.isMaximized()) {
                        primaryStage.setMaximized(false);
                    } else {
                        primaryStage.setMaximized(true);
                    }
                    break;
                case F9:
                    primaryStage.setIconified(true);
                    break;
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}