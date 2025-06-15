package com.controller;

import com.database.DatabaseConnection;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ProductControllerTest {

    private ProductController productController;
    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Koneksi database untuk tes tidak boleh null.");
        conn.setAutoCommit(false); 

        productController = new ProductController();
       
        productController.logoImageView = new ImageView();
        productController.promotionNavButton = new Button();
        productController.cartNavButton = new Button();
        productController.shopNavButton = new Button();
        productController.orderNavButton = new Button();
        productController.backToShopLink = new Hyperlink();
        productController.productTitleLabel = new Label();
        productController.productPriceLabel = new Label();
        productController.productDescriptionLabel = new Label();
        productController.productImageView = new ImageView();
        productController.ratingSummaryLabel = new Label();
        productController.reviewCountLabel = new Label();
        productController.reviewListContainer = new VBox();
        productController.viewMoreButton = new Button();
        productController.viewLessButton = new Button();
        productController.decreaseQuantityButton = new Button();
        productController.quantityLabel = new Label();
        productController.increaseQuantityButton = new Button();
        productController.checkoutButton = new Button();
    }


    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback(); 
            conn.close();
        }
    }

    
    @Test
    @DisplayName("Test tombol kuantitas harus tetap berfungsi")
    void testQuantityButtons_ShouldWorkCorrectly() {
        
        assertEquals(1, productController.quantity.get(), "Quantity harus dimulai dari 1.");

        productController.handleIncreaseQuantity();
        assertEquals(2, productController.quantity.get(), "Quantity seharusnya meningkat menjadi 2.");
        
        productController.handleDecreaseQuantity();
        assertEquals(1, productController.quantity.get(), "Quantity seharusnya menurun menjadi 1.");

       
        productController.handleDecreaseQuantity();
        assertEquals(1, productController.quantity.get(), "Quantity seharusnya tetap 1 dan tidak kurang dari itu.");
    }
}