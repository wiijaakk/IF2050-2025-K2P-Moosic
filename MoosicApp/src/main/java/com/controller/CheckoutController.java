package com.controller;

import com.database.CheckoutDatabaseConnection;
import com.database.CheckoutDatabaseQuery;
import com.model.DiscountPromo;
import com.model.Order;
import com.model.OrderItem;
import com.model.Product;
import com.model.AllProduct;
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
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

public class CheckoutController {

    private Order currentOrder;
    
    private Product selectedProduct;
    private AllProduct selectedAllProduct;
    private int quantity = 1;

    @FXML private Button payButton;
    @FXML private Button exitButton;
    @FXML private Button backButton;
    @FXML private Button logoButton;
    @FXML private Button shopNavButton;
    @FXML private Label navPromotion;
    @FXML private Button navCart;
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
    
    @FXML private ImageView productImageView;
    @FXML private Label productNameLabel;
    @FXML private Label priceLabel;
    @FXML private Label quantityLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label finalTotalLabel;

    @FXML private BorderPane mainContainer;

    @FXML
    public void initialize() {
        this.currentOrder = new Order();
        
        if (selectedProduct == null && selectedAllProduct == null) {
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
        if (navCart != null) {
            navCart.getStyleClass().add("active");
        }

        nameField.setOnAction(e -> mobilePhoneField.requestFocus());
        mobilePhoneField.setOnAction(e -> addressField.requestFocus());
        addressField.setOnAction(e -> countryField.requestFocus());
        countryField.setOnAction(e -> stateField.requestFocus());
        stateField.setOnAction(e -> cityField.requestFocus());
        cityField.setOnAction(e -> zipCodeField.requestFocus());
        zipCodeField.setOnAction(e -> cardNameField.requestFocus());
        cardNameField.setOnAction(e -> cardNumberField.requestFocus());
        cardNumberField.setOnAction(e -> validThroughField.requestFocus());
        validThroughField.setOnAction(e -> cvvField.requestFocus());
        cvvField.setOnAction(e -> promoCodeField.requestFocus());
        promoCodeField.setOnAction(e -> handlePayButtonAction(new ActionEvent(payButton, payButton)));
    }
    
    public void setProductData(Product product, int qty) {
        this.selectedProduct = product;
        this.selectedAllProduct = null;
        this.quantity = qty;
        
        System.out.println("✅ Product data received:");
        System.out.println("   Product: " + product.getName());
        System.out.println("   Quantity: " + qty);
        System.out.println("   Price: Rp" + String.format("%,.0f", product.getPrice().doubleValue()));
        
        if (productNameLabel != null) {
            loadProductToUI();
        }
    }
    
    public void setAllProductData(AllProduct product, int qty) {
        this.selectedAllProduct = product;
        this.selectedProduct = null;
        this.quantity = qty;
        
        System.out.println("✅ AllProduct data received:");
        System.out.println("   Product: " + product.getName());
        System.out.println("   Quantity: " + qty);
        System.out.println("   Price: Rp" + String.format("%,.0f", product.getPrice()));
        
        if (productNameLabel != null) {
            loadProductToUI();
        }
    }
    
    private String getCurrentProductName() {
        if (selectedProduct != null) return selectedProduct.getName();
        if (selectedAllProduct != null) return selectedAllProduct.getName();
        return "Unknown Product";
    }
    
    private double getCurrentProductPrice() {
        if (selectedProduct != null) return selectedProduct.getPrice().doubleValue();
        if (selectedAllProduct != null) return selectedAllProduct.getPrice();
        return 0.0;
    }
    
    private String getCurrentProductImage() {
        if (selectedProduct != null) return selectedProduct.getGambar();
        if (selectedAllProduct != null) return selectedAllProduct.getImage();
        return "midnight-ts-vinyl.jpg";
    }
    
    private void loadProductToUI() {
        if (selectedProduct == null && selectedAllProduct == null) return;
        
        System.out.println("🔄 Loading product data to UI...");
        
        if (productNameLabel != null) {
            String productName = getCurrentProductName();
            productNameLabel.setText(productName);
            System.out.println("   ✅ Product name loaded: " + productName);
        }
        
        if (priceLabel != null) {
            double priceValue = getCurrentProductPrice();
            priceLabel.setText(String.format("Rp%,.0f", priceValue));
            System.out.println("   ✅ Price loaded: Rp" + String.format("%,.0f", priceValue));
        }
        
        if (quantityLabel != null) {
            quantityLabel.setText("x " + quantity);
            System.out.println("   ✅ Quantity loaded: x" + quantity);
        }
        
        double totalPrice = getCurrentProductPrice() * quantity;
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
        if (productImageView == null) return;
        
        try {
            String imagePath = getCurrentProductImage();
            
            if (!imagePath.startsWith("/")) {
                imagePath = "/image/album/" + imagePath;
            }
            
            System.out.println("🖼️ Trying to load image: " + imagePath);
            
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            
            if (!image.isError()) {
                productImageView.setImage(image);
                System.out.println("   ✅ Image loaded successfully: " + imagePath);
            } else {
                System.out.println("   ❌ Image error, using default image");
                useDefaultImage();
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Error loading image: " + e.getMessage());
            useDefaultImage();
        }
    }
    
    private void useDefaultImage() {
        try {
            String[] defaultPaths = {
                "/image/album/midnight-ts-vinyl.jpg",
                "/image/album/midnights.png",
                "/image/placeholder.png"
            };
            
            for (String defaultPath : defaultPaths) {
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream(defaultPath));
                    if (productImageView != null && !defaultImage.isError()) {
                        productImageView.setImage(defaultImage);
                        System.out.println("   ✅ Default image loaded: " + defaultPath);
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("   ❌ " + defaultPath + " not found");
                }
            }
            
            System.out.println("   ❌ No default images found");
            
        } catch (Exception e) {
            System.out.println("   ❌ Error loading default image: " + e.getMessage());
        }
    }
    
    private void updateOrderModel() {
        if (selectedProduct == null && selectedAllProduct == null) return;
        
        currentOrder = new Order();
        
        if (selectedProduct != null) {
            OrderItem item = new OrderItem(selectedProduct, quantity);
            currentOrder.addItem(item);
        } else if (selectedAllProduct != null) {
            Product tempProduct = new Product(
                selectedAllProduct.getId(),
                selectedAllProduct.getName(),
                selectedAllProduct.getDescription(),
                BigDecimal.valueOf(selectedAllProduct.getPrice()),
                selectedAllProduct.getGenre(),
                selectedAllProduct.getVariant(),
                selectedAllProduct.getImage(),
                new ArrayList<>()
            );
            OrderItem item = new OrderItem(tempProduct, quantity);
            currentOrder.addItem(item);
        }
        
        System.out.println("✅ Order model updated with current product");
    }

    @FXML
    private void handleLogoNavigationToHome(ActionEvent event) {
        System.out.println("🏠 Logo clicked, navigating to Homepage (Full Screen)...");
        navigateToPage(event, "/fxml/homepage.fxml", "Moosic - Home");
    }
    
    @FXML
    private void handleShopButtonAction(ActionEvent event) {
        System.out.println("🛍️ Navigating to Shop page (Full Screen)...");
        navigateToPage(event, "/fxml/Shop.fxml", "Moosic - Shop");
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        System.out.println("🔙 Navigating back to Product page (Full Screen)...");
        
        String[] productPaths = {
            "/fxml/Product.fxml",
            "/fxml/product.fxml"
        };
        
        boolean success = false;
        for (String productPath : productPaths) {
            try {
                System.out.println("🔍 Trying product path: " + productPath);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(productPath));
                Parent root = loader.load();
                
                ProductController productController = loader.getController();
                if (productController != null) {
                    if (selectedProduct != null) {
                        productController.initData(selectedProduct.getId());
                        System.out.println("   ✅ Product initialized with ID: " + selectedProduct.getId());
                    } else if (selectedAllProduct != null) {
                        productController.initData(selectedAllProduct.getId());
                        System.out.println("   ✅ AllProduct initialized with ID: " + selectedAllProduct.getId());
                    } else {
                        productController.initData(1);
                        System.out.println("   ✅ Default product initialized with ID: 1");
                    }
                }
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                boolean wasMaximized = stage.isMaximized();
                double currentWidth = stage.getWidth();
                double currentHeight = stage.getHeight();
                
                Scene scene;
                if (wasMaximized) {
                    scene = new Scene(root, stage.getWidth(), stage.getHeight());
                } else {
                    scene = new Scene(root, currentWidth, currentHeight);
                    wasMaximized = true;
                }
                
                try {
                    String cssUrl = getClass().getResource("/css/product.css").toExternalForm();
                    scene.getStylesheets().add(cssUrl);
                    System.out.println("✅ Loaded product CSS");
                } catch (Exception e) {
                    System.out.println("⚠️ Product CSS not found - " + e.getMessage());
                }
                
                stage.setScene(scene);
                stage.setTitle("Moosic - Product Details");
                
                ensureMaximizedWindow(stage);
                
                stage.show();
                
                System.out.println("✅ Successfully navigated to Product page using: " + productPath + " (Full Screen)");
                success = true;
                break;
                
            } catch (Exception e) {
                System.out.println("❌ " + productPath + " failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (!success) {
            System.err.println("❌ Could not navigate to Product page!");
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Could not navigate to Product page.\n\n" +
                     "Possible issues:\n" +
                     "1. Product.fxml file not found in /fxml/ folder\n" +
                     "2. ProductController has missing methods\n" +
                     "3. CSS file missing\n" +
                     "4. Database connection issue");
        }
    }

    private void navigateToPage(ActionEvent event, String fxmlPath, String title) {
        try {
            System.out.println("🔍 Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            boolean wasMaximized = stage.isMaximized();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            double currentX = stage.getX();
            double currentY = stage.getY();
            
            Scene scene;
            if (wasMaximized) {
                scene = new Scene(root, stage.getWidth(), stage.getHeight());
            } else {
                scene = new Scene(root, currentWidth, currentHeight);
                wasMaximized = true;
            }

            String cssPath = null;
            if (fxmlPath.contains("homepage")) {
                cssPath = "/css/homepage.css";
            } else if (fxmlPath.contains("Shop")) {
                cssPath = "/css/shopstyle.css";
            } else if (fxmlPath.contains("product")) {
                cssPath = "/css/product.css";
            } else if (fxmlPath.contains("SuccessPage")) {
                cssPath = "/css/successpage.css";
            }
            
            if (cssPath != null) {
                try {
                    String cssUrl = getClass().getResource(cssPath).toExternalForm();
                    scene.getStylesheets().add(cssUrl);
                    System.out.println("✅ Loaded CSS: " + cssPath);
                } catch (Exception e) {
                    System.out.println("⚠️ CSS not found: " + cssPath + " - " + e.getMessage());
                }
            }
            
            stage.setScene(scene);
            stage.setTitle(title);
            
            ensureMaximizedWindow(stage);
            
            stage.show();
            System.out.println("✅ Successfully navigated to " + title + " (Full Screen)");
            
        } catch (IOException e) {
            System.err.println("❌ IO Error navigating to " + title + ": " + e.getMessage());
            System.err.println("❌ Failed FXML path: " + fxmlPath);
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Could not navigate to " + title + ". File not found: " + fxmlPath);
        } catch (ClassCastException e) {
            System.err.println("❌ ClassCastException in navigateToPage: " + e.getMessage());
            System.err.println("This often happens when navigateToPage is called from a non-Node source (like a Task).");
            e.printStackTrace();
            try {
                Stage stage = (Stage) payButton.getScene().getWindow(); 
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

                String cssPath = null;
                if (fxmlPath.contains("homepage")) {
                    cssPath = "/css/homepage.css";
                } else if (fxmlPath.contains("Shop")) {
                    cssPath = "/css/shopstyle.css";
                } else if (fxmlPath.contains("product")) {
                    cssPath = "/css/product.css";
                } else if (fxmlPath.contains("SuccessPage")) {
                    cssPath = "/css/successpage.css";
                }
                if (cssPath != null) {
                    try {
                        String cssUrl = getClass().getResource(cssPath).toExternalForm();
                        scene.getStylesheets().add(cssUrl);
                    } catch (Exception cssE) {
                        System.out.println("⚠️ CSS not found during ClassCastException recovery: " + cssPath + " - " + cssE.getMessage());
                    }
                }
                stage.setScene(scene);
                stage.setTitle(title);
                ensureMaximizedWindow(stage);
                stage.show();
                System.out.println("✅ Successfully navigated to " + title + " (Full Screen) via ClassCastException recovery.");
            } catch (Exception recoveryE) {
                System.err.println("❌ Error during ClassCastException recovery: " + recoveryE.getMessage());
                recoveryE.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                         "An error occurred during navigation: " + recoveryE.getMessage());
            }
        }
        catch (Exception e) {
            System.err.println("❌ Unexpected error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                     "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void ensureMaximizedWindow(Stage stage) {
        try {
            stage.setMaximized(false);
            stage.setMaximized(true);
            
            Platform.runLater(() -> {
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
    
    private void forceFullScreen(Stage stage) {
        try {
            stage.setMaximized(false);
            
            Platform.runLater(() -> {
                stage.setMaximized(true);
                
                Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(100), 
                    e -> {
                        if (!stage.isMaximized()) {
                            stage.setMaximized(true);
                            System.out.println("🔄 Re-applied maximized state");
                        }
                    }
                ));
                timeline.play();
            });
            
        } catch (Exception e) {
            System.err.println("⚠️ Error forcing full screen: " + e.getMessage());
        }
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
                navigateToPage(new ActionEvent(payButton, payButton), "/fxml/SuccessPage.fxml", "Moosic - Payment Success"); 
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