package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SuccessPageController {

    @FXML
    private Button returnButton;

    @FXML
    private void handleReturntoHome(ActionEvent event) {
        // TODO: Tambahkan navigasi ke halaman utama di sini
        System.out.println("Returning to Home...");
    }
}
