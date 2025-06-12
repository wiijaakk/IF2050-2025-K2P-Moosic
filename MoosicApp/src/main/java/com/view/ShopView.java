package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ShopView extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/Shop.fxml")));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/shopstyle.css")).toExternalForm());

        primaryStage.setTitle("Moosic Shop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}