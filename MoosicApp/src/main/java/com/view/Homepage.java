package com.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;

public class Homepage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MoosicApp");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepage.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);


        URL cssUrl = getClass().getResource("/css/homepage.css");
        if (cssUrl == null) {
            System.err.println("Error: loginstyle.css not found! Please ensure it's in the correct directory (e.g., in the root of your classpath).");
        } else {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}