package com.controller;

import com.database.CheckoutDatabaseConnection;
import com.database.CheckoutDatabaseQuery;
import com.model.DiscountPromo;
import com.model.Order;
import com.model.OrderItem;
import com.model.Product;
import com.model.ProductReview;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

public class CheckoutController {

    private Order currentOrder;
    
    private Product selectedProduct;
    private int quantity = 1;

    @FXML private Button payButton;
    @FXML private Button exitButton;
    @FXML private Button backButton; 
    @FXML private Button logoButton;  
    @FXML private Button shopNavButton;  
    @FXML private Label navPromotion;
    @FXML private Label navCart;
    @FXML private Button navShop;
    @FXML private Label navOrder;
    @FXML private TextField nameField;
    @FXML private TextField mobilePhoneField;
    @FXML private TextField addressField;
    @FXML private TextField countryField;
    @FXML private TextField stateField;
    @FXML private TextField cityField;
    @FXML private TextField zipCodeField;
    @FXML private TextField cardNameField;
    @FXML private TextField cardNumberField;
    @FXML private TextField validThroughField;
    @FXML private TextField cvvField;
    @FXML private TextField promoCodeField;
    
    // FXML elements untuk product display
    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label priceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label finalTotalLabel;

    @FXML
    public void initialize() {
        this.currentOrder = new Order();
        
        if (selectedProduct == null) {
            selectedProduct = new Product(1, "Midnights Vinyl – Taylor Swift", 
                "Exclusive \"Midnights\" edition by Taylor Swift", 
                new BigDecimal("525000.00"), "Pop", "Vinyl", 
                "midnight-ts-vinyl.jpg", new ArrayList<>());
        }
        
        loadProductToUI();
        
        setupTextFieldFocusListeners(nameField);
        setupTextFieldFocusListeners(mobilePhoneField);
        setupTextFieldFocusListeners(addressField);
        setupTextFieldFocusListeners(countryField);
        setupTextFieldFocusListeners(stateField);
        setupTextFieldFocusListeners(cityField);
        setupTextFieldFocusListeners(zipCodeField);
        setupTextFieldFocusListeners(cardNameField);
        setupTextFieldFocusListeners(cardNumberField);
        setupTextFieldFocusListeners(validThroughField);
        setupTextFieldFocusListeners(cvvField);
        setupTextFieldFocusListeners(promoCodeField);

        if (navShop != null){
            navShop.getStyleClass().add("active");
        }
    }
    
    public void setProductData(Product product, int qty) {
        this.selectedProduct = product;
        this.quantity = qty;
        
        System.out.println("✅ Product data received:");
        System.out.println("   Product: " + product.getName());
        System.out.println("   Quantity: " + qty);
        System.out.println("   Price: Rp" + String.format("%,.0f", product.getPrice().doubleValue()));
        
        if (productNameLabel != null) {
            loadProductToUI();
        }
    }
    
    private void loadProductToUI() {
        if (selectedProduct == null) return;
        
        System.out.println("🔄 Loading product data to UI...");
        
        if (productNameLabel != null) {
            productNameLabel.setText(selectedProduct.getName());
            System.out.println("   ✅ Product name loaded: " + selectedProduct.getName());
        }
        
        if (priceLabel != null) {
            double priceValue = selectedProduct.getPrice().doubleValue();
            priceLabel.setText(String.format("Rp%,.0f", priceValue));
            System.out.println("   ✅ Price loaded: Rp" + String.format("%,.0f", priceValue));
        }
        
        if (quantityLabel != null) {
            quantityLabel.setText("x " + quantity);
            System.out.println("   ✅ Quantity loaded: x" + quantity);
        }
        
        double totalPrice = selectedProduct.getPrice().doubleValue() * quantity;
        if (totalPriceLabel != null) {
            totalPriceLabel.setText(String.format("Rp%,.0f", totalPrice));
            System.out.println("   ✅ Total price loaded: Rp" + String.format("%,.0f", totalPrice));
        }
        
        if (subtotalLabel != null) {
            subtotalLabel.setText(String.format("Rp%,.0f", totalPrice));
        }
        
        if (finalTotalLabel != null) {
            finalTotalLabel.setText(String.format("Rp%,.0f", totalPrice));
        }
        
        loadProductImage();
        
        updateOrderModel();
        
        System.out.println("✅ All product data loaded successfully!");
    }
    
    private void loadProductImage() {
        if (selectedProduct == null || productImageView == null) return;
        
        try {
            String imagePath = "/image/album/" + selectedProduct.getGambar();
            System.out.println("🖼️ Trying to load image: " + imagePath);
            
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            
            if (!image.isError()) {
                productImageView.setImage(image);
                System.out.println("Image loaded successfully: " + imagePath);
            } else {
                System.out.println("Image error, using default image");
                useDefaultImage();
            }
            
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            useDefaultImage();
        }
    }
    
    private void useDefaultImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/image/album/midnight-ts-vinyl.jpg"));
            if (productImageView != null && !defaultImage.isError()) {
                productImageView.setImage(defaultImage);
                System.out.println("Default image loaded: midnight-ts-vinyl.jpg");
            } else {
                System.out.println("midnight-ts-vinyl.jpg not found, trying other options...");
                
                String[] alternatives = {
                    "/image/album/midnight-ts-cd.jpg",
                    "/image/album/midnight-ts-cassette.jpg"
                };
                
                for (String altPath : alternatives) {
                    try {
                        Image altImage = new Image(getClass().getResourceAsStream(altPath));
                        if (!altImage.isError()) {
                            productImageView.setImage(altImage);
                            System.out.println("Alternative image loaded: " + altPath);
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println(altPath + " also not found");
                    }
                }
                
                System.out.println("No album images found");
            }
        } catch (Exception e) {
            System.out.println("Error loading default album image: " + e.getMessage());
        }
    }
    
    private void updateOrderModel() {
        if (selectedProduct == null) return;
        
        currentOrder = new Order();
        
        OrderItem item = new OrderItem(selectedProduct, quantity);
        currentOrder.addItem(item);
        
        System.out.println("Order model updated with current product");
    }

    @FXML
    private void handleLogoButtonAction(ActionEvent event) {
        System.out.println("Logo button clicked - no action implemented yet");
        // insert logic
    }
    
    @FXML
    private void handleShopButtonAction(ActionEvent event) {
        System.out.println("Shop button clicked - no action implemented yet");
        // insert logic
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("Back button clicked - no action implemented yet");
        // insert logic
    }
    
    @FXML
    private void handleLogoButtonEntered(MouseEvent event) {
        if (logoButton != null) {
            logoButton.setStyle("-fx-background-color: rgba(146, 172, 79, 0.1); -fx-border-color: transparent; -fx-cursor: hand; -fx-background-radius: 5;");
        }
    }
    
    @FXML
    private void handleLogoButtonExited(MouseEvent event) {
        if (logoButton != null) {
            logoButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-cursor: hand;");
        }
    }
    
    @FXML
    private void handleShopButtonEntered(MouseEvent event) {
        if (shopNavButton != null) {
            shopNavButton.setStyle("-fx-background-color: rgba(146, 172, 79, 0.1); -fx-text-fill: #4D7111; -fx-font-size: 16px; -fx-padding: 8px 20px; -fx-background-radius: 5;");
        }
    }
    
    @FXML
    private void handleShopButtonExited(MouseEvent event) {
        if (shopNavButton != null) {
            shopNavButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #92AC4F; -fx-font-size: 16px; -fx-padding: 8px 20px;");
        }
    }
    
    @FXML
    private void handleBackButtonEntered(MouseEvent event) {
        if (backButton != null) {
            backButton.setStyle("-fx-background-color: #A8D665; -fx-background-radius: 8;");
        }
    }
    
    @FXML
    private void handleBackButtonExited(MouseEvent event) {
        if (backButton != null) {
            backButton.setStyle("-fx-background-color: #C4EA57; -fx-background-radius: 8;");
        }
    }

    @FXML
    private void handlePayButtonAction(ActionEvent event) {
        if (nameField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Lengkap", "Harap isi Nama dan Alamat.");
            return;
        }

        String promoCodeInput = promoCodeField.getText().trim();
        DiscountPromo validPromo = null;

        if (promoCodeInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Kode Promo Wajib", "Anda harus memasukkan kode promo untuk melanjutkan.");
            return; 
        }

        validPromo = CheckoutDatabaseQuery.validatePromoCode(promoCodeInput);
        if (validPromo == null) {
            showAlert(Alert.AlertType.ERROR, "Promo Tidak Valid", "Invalid promo code!");
            return; 
        }
        
        populateOrderModel();

        if (validPromo != null) {
            BigDecimal subtotal = BigDecimal.valueOf(currentOrder.calculateSubtotal());
            BigDecimal discountPercentage = validPromo.getPersentase().divide(new BigDecimal(100));
            BigDecimal discountAmount = subtotal.multiply(discountPercentage).setScale(2, RoundingMode.HALF_UP);
            currentOrder.setDiscount(discountAmount.doubleValue());
            currentOrder.setPromoCode(validPromo.getPromo_code());
        }

        Task<Boolean> saveOrderTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                int customerId = 1;
                return CheckoutDatabaseQuery.placeOrder(currentOrder, customerId);
            }
        };

        saveOrderTask.setOnSucceeded(e -> {
            boolean success = saveOrderTask.getValue();
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Pesanan Anda telah berhasil disimpan!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Terjadi kesalahan saat menyimpan pesanan.");
            }
            payButton.setDisable(false);
            payButton.setText("Pay Now");
        });

        saveOrderTask.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Error Kritis", "Terjadi error tak terduga.");
            saveOrderTask.getException().printStackTrace();
            payButton.setDisable(false);
            payButton.setText("Pay Now");
        });

        payButton.setDisable(true);
        payButton.setText("Processing...");
        new Thread(saveOrderTask).start();
    }
    
    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleExitButtonEntered(MouseEvent event) {
        if (exitButton != null) exitButton.getStyleClass().add("exit-button-hover");
    }

    @FXML
    private void handleExitButtonExited(MouseEvent event) {
        if (exitButton != null) exitButton.getStyleClass().remove("exit-button-hover");
    }

    @FXML
    private void onNavItemEntered(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.getStyleClass().add("nav-label-hover");
    }

    @FXML
    private void onNavItemExited(MouseEvent event) {
        Node source = (Node) event.getSource();
        source.getStyleClass().remove("nav-label-hover");
    }

    @FXML
    private void handlePayButtonEntered(MouseEvent event) {
        payButton.getStyleClass().add("pay-button-hover");
    }

    @FXML
    private void handlePayButtonExited(MouseEvent event) {
        payButton.getStyleClass().remove("pay-button-hover");
    }

    private void setupTextFieldFocusListeners(TextField field) {
        if (field != null) {
            field.getStyleClass().add("text-field");
            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    field.getStyleClass().add("text-field-focused");
                } else {
                    field.getStyleClass().remove("text-field-focused");
                }
            });
        }
    }
    
    private void populateOrderModel() {
        currentOrder.setShippingName(nameField.getText());
        currentOrder.setShippingMobilePhone(mobilePhoneField.getText());
        currentOrder.setShippingAddress(addressField.getText());
        currentOrder.setShippingCountry(countryField.getText());
        currentOrder.setShippingState(stateField.getText());
        currentOrder.setShippingCity(cityField.getText());
        currentOrder.setShippingZipCode(zipCodeField.getText());
        currentOrder.setCardFullName(cardNameField.getText());
        currentOrder.setCardNumber(cardNumberField.getText());
        currentOrder.setCardExpirationDate(validThroughField.getText());
        currentOrder.setCardCvv(cvvField.getText());
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}