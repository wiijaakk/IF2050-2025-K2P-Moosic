package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane; // Atau layout kontainer yang sesuai
import javafx.scene.layout.BorderPane; // Jika BorderPane adalah root
import java.io.IOException;

public class DashboardController {

    @FXML private AnchorPane mainContent; // fx:id dari fx:include di FXML (atau kontainer Parent lainnya)
    // Jika Anda menggunakan BorderPane sebagai root dan ingin mengganti center:
    // @FXML private BorderPane rootPane;

    @FXML
    public void initialize() {
        // Inisialisasi awal jika diperlukan
    }

    @FXML
    private void onRegisterClicked() {
        loadPage("/fxml/LoginView.fxml");
    }

    @FXML
    private void handleLoginButtonAction() {
        loadPage("/fxml/homepage.fxml");
    }

    @FXML
    private void handleRegisterLinkAction() {
        loadPage("/fxml/register.fxml");
    }

    @FXML
    private void handleLogoNavigationToHome() {
        loadPage("/fxml/homepage.fxml");
    }

    @FXML
    private void handleShopButtonAction() {
        loadPage("/fxml/Shop.fxml");
    }

    @FXML
    private void handleBackButtonAction() {
        loadPage("/fxml/product.fxml");
    }

    @FXML
    private void handlePayButtonAction() {
        loadPage("/fxml/SuccessPage.fxml");
    }

    @FXML
    private void handleCheckoutAction() {
        loadPage("/fxml/Checkout.fxml");
    }

    @FXML
    private void handleBackToShopAction() {
        loadPage("/fxml/Shop.fxml");
    }



    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            
            // Ganti konten di dalam mainContent (yang awalnya EmptyView.fxml)
            mainContent.getChildren().setAll(page);
            // Atau jika menggunakan BorderPane sebagai root dan mainContent adalah center:
            // rootPane.setCenter(page);

            // Sesuaikan ukuran jika perlu, tergantung pada layout kontainer
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

        } catch (IOException e) {
            System.err.println("Failed to load FXML page: " + fxmlPath + ". Error: " + e.getMessage());
            e.printStackTrace();
            // Handle error, misalnya menampilkan pesan error di UI
        }
    }
}