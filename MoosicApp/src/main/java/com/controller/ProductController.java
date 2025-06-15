package com.controller;

import com.database.ProductDAO;
import com.model.Product;
import com.model.ProductReview;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML Button logoButton;
    @FXML Button promotionNavButton;
    @FXML Button cartNavButton;
    @FXML Button shopNavButton;
    @FXML Button orderNavButton;
    @FXML Hyperlink backToShopLink;
    @FXML Label productTitleLabel;
    @FXML Label productPriceLabel;
    @FXML Label productDescriptionLabel;
    @FXML ImageView productImageView;
    @FXML Label ratingSummaryLabel;
    @FXML Label reviewCountLabel;
    @FXML VBox reviewListContainer;
    @FXML Button viewMoreButton;
    @FXML Button viewLessButton;
    @FXML Label quantityLabel;
    @FXML Button checkoutButton;
    @FXML BorderPane mainContainer;
    @FXML StackPane searchBarContainer;

    Product currentProduct;
    List<ProductReview> allProductReviews;
    static final int INITIAL_REVIEW_COUNT = 2;
    final IntegerProperty quantity = new SimpleIntegerProperty(1);

    @FXML ImageView logoImageView;
    @FXML Button decreaseQuantityButton;
    @FXML Button increaseQuantityButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (quantityLabel != null) {
            quantityLabel.textProperty().bind(quantity.asString());
        }

        if (shopNavButton != null) {
            shopNavButton.getStyleClass().add("active");
        }
    }

    public void initData(int productId) {
        this.currentProduct = ProductDAO.getProductById(productId);

        if (this.currentProduct != null) {
            this.allProductReviews = this.currentProduct.getReviews();
            populateProductData(this.currentProduct);
            updateReviewSummary();
            displayReviews(INITIAL_REVIEW_COUNT);
        } else {
            if (productTitleLabel != null) {
                productTitleLabel.setText("Product Not Found");
            }
            if (reviewListContainer != null) {
                reviewListContainer.getChildren().add(new Label("Could not load product details."));
            }
            if (checkoutButton != null) {
                checkoutButton.setDisable(true);
            }
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(false);
            }
            if (viewLessButton != null) {
                viewLessButton.setVisible(false);
            }
        }
    }

    private void populateProductData(Product product) {
        if (productTitleLabel != null) {
            productTitleLabel.setText(product.getName());
        }
        if (productDescriptionLabel != null) {
            productDescriptionLabel.setText(product.getDescription());
        }
        if (productPriceLabel != null) {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            productPriceLabel.setText(currencyFormatter.format(product.getPrice()));
        }

        if (productImageView != null) {
            Image image = createSafeImage(product.getGambar());
            productImageView.setImage(image != null ? image : createSafeImage("/image/placeholder.png"));
        }
    }

    private void updateReviewSummary() {
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            if (ratingSummaryLabel != null) {
                ratingSummaryLabel.setText("N/A");
            }
            if (reviewCountLabel != null) {
                reviewCountLabel.setText("from 0 reviews");
            }
            return;
        }
        double averageRating = allProductReviews.stream()
                .mapToDouble(ProductReview::getStar)
                .average()
                .orElse(0.0);
        if (ratingSummaryLabel != null) {
            ratingSummaryLabel.setText(String.format("%.1f / 5", averageRating));
        }
        if (reviewCountLabel != null) {
            reviewCountLabel.setText("from " + allProductReviews.size() + " reviews");
        }
    }

    private void displayReviews(int limit) {
        if (reviewListContainer != null) {
            reviewListContainer.getChildren().clear();
        }
        
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            if (reviewListContainer != null) {
                reviewListContainer.getChildren().add(new Label("No reviews for this product yet."));
            }
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(false);
            }
            if (viewLessButton != null) {
                viewLessButton.setVisible(false);
            }
            return;
        }

        allProductReviews.stream().limit(limit).forEach(review -> {
            if (reviewListContainer != null) {
                reviewListContainer.getChildren().add(createReviewCard(review));
            }
        });

        boolean hasMoreReviews = allProductReviews.size() > INITIAL_REVIEW_COUNT;
        boolean isExpanded = limit >= allProductReviews.size();
        
        if (hasMoreReviews) {
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(!isExpanded);
            }
            if (viewLessButton != null) {
                viewLessButton.setVisible(isExpanded);
            }
        } else {
            if (viewMoreButton != null) {
                viewMoreButton.setVisible(false);
            }
            if (viewLessButton != null) {
                viewLessButton.setVisible(false);
            }
        }
    }

    private HBox createReviewCard(ProductReview review) {
        HBox card = new HBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        ImageView userIcon = createSafeImageView("/image/Account circle.png", 40, 40);
        VBox textContainer = new VBox(5);
        HBox userDateContainer = new HBox(8);
        Label usernameLabel = new Label(review.getCustomerUsername());
        usernameLabel.getStyleClass().add("review-user");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        Label dateLabel = new Label(review.getCreatedAt().format(dateFormatter));
        dateLabel.getStyleClass().add("review-date");
        userDateContainer.getChildren().addAll(usernameLabel, dateLabel);
        Label commentLabel = new Label(review.getReview());
        commentLabel.getStyleClass().add("review-text");
        commentLabel.setWrapText(true);
        textContainer.getChildren().addAll(userDateContainer, commentLabel);
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox ratingContainer = new HBox(5);
        ImageView starIcon = createSafeImageView("/image/star.png", 20, 20);
        Label ratingLabel = new Label(String.valueOf(review.getStar()));
        ratingLabel.getStyleClass().add("review-rating");
        ratingContainer.getChildren().addAll(starIcon, ratingLabel);
        card.getChildren().addAll(userIcon, textContainer, spacer, ratingContainer);
        return card;
    }
    
    
    @FXML
    private void handleCheckout() {
        System.out.println("🛒 Navigating to Checkout page (inline)...");

        try {
            System.out.println("🔍 DEBUG - Current product data before checkout:");
            if (currentProduct != null) {
                System.out.println(" Product: " + currentProduct.getName());
                System.out.println(" Price: " + currentProduct.getPrice());
            } else {
                System.out.println(" ❌ currentProduct is NULL!");
            }
            System.out.println(" Quantity: " + quantity.get());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Checkout.fxml"));
            Parent checkoutPage = loader.load();

            CheckoutController checkoutController = loader.getController();
            
            if (checkoutController == null) {
                System.out.println("❌ CheckoutController is NULL!");
                throw new RuntimeException("Failed to get CheckoutController");
            }

            checkoutController.setProductData(currentProduct, quantity.get());

            System.out.println("✅ Product data passed to checkout:");
            System.out.println(" Product: " + currentProduct.getName());
            System.out.println(" Quantity: " + quantity.get());
            System.out.println(" Price: " + currentProduct.getPrice());

            if (mainContainer != null) {
                mainContainer.setTop(null);
                mainContainer.setBottom(null);
                mainContainer.setCenter(null);
                mainContainer.setCenter(checkoutPage);
            }

            try {
                String cssPath = getClass().getResource("/css/checkout.css").toExternalForm();
                checkoutPage.getStylesheets().add(cssPath);
                System.out.println("✅ Loaded Checkout CSS from: " + cssPath);
            } catch (Exception e) {
                System.out.println("⚠️ Checkout CSS not found - " + e.getMessage());
            }

            System.out.println("✅ Successfully navigated to Checkout page (inline)");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to load Checkout.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                "Could not navigate to Checkout page. File not found.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Unexpected error during checkout navigation: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Generic navigation method with FULL SCREEN enforcement
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param cssPath Path to CSS file (can be null)
     * @param sourceNode Node for getting current stage
     */
    private void navigateToPageFullScreen(String fxmlPath, String title, String cssPath, Node sourceNode) {
        try {
            System.out.println("🔍 Attempting to load FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            
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
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                     "Could not navigate to " + title + ". File not found: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error navigating to " + title + ": " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                     "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Enhanced method untuk memastikan window full screen
     * @param stage The JavaFX Stage to maximize
     */
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


    @FXML 
    public void handleViewMore() {
        if (allProductReviews != null) {
            displayReviews(allProductReviews.size());
        }
    }

    @FXML 
    public void handleViewLess() {
        displayReviews(INITIAL_REVIEW_COUNT);
    }

    @FXML 
    public void handleIncreaseQuantity() {
        quantity.set(quantity.get() + 1);
    }

    @FXML 
    public void handleDecreaseQuantity() {
        if (quantity.get() > 1) {
            quantity.set(quantity.get() - 1);
        }
    }

    @FXML
    private void handleBackToShop() {
        System.out.println("🔙 Navigating back to Shop page (inline)...");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Shop.fxml"));
            Parent shopPage = loader.load();

            if (mainContainer != null) {
                mainContainer.setTop(null);
                mainContainer.setBottom(null);
                mainContainer.setCenter(null);
                mainContainer.setCenter(shopPage);
            }

            try {
                String cssPath = getClass().getResource("/css/shopstyle.css").toExternalForm();
                shopPage.getStylesheets().add(cssPath);
                System.out.println("Loading Shop CSS from: " + cssPath);
            } catch (Exception e) {
                System.out.println("Shop CSS not found, using default styling");
            }

            System.out.println("Successfully navigated back to Shop page (inline)");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open shop page: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogoNavigationToHome(ActionEvent event) {
        System.out.println("Logo clicked, navigating to Home page (inline)...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/homepage.fxml"));
            Parent homePage = loader.load();

            mainContainer.setTop(null);
            mainContainer.setBottom(null);
            mainContainer.setCenter(null);
            mainContainer.setCenter(homePage);

            try {
                String cssPath = getClass().getResource("/css/homepage.css").toExternalForm();
                homePage.getStylesheets().add(cssPath);
                System.out.println("Loading Home CSS from: " + cssPath);
            } catch (Exception e) {
                System.out.println("Home CSS not found, using default styling");
            }

            System.out.println("Switched to Home Page (inline)");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open home page: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                "An unexpected error occurred: " + e.getMessage());
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

    @FXML private void handlePromotionNav() {
        System.out.println("🎯 Navigating to Promotion page (Full Screen)...");
    }

    @FXML private void handleCartNav() {
        System.out.println("🛒 Navigating to Cart page (Full Screen)...");
    }

    @FXML private void handleShopNav() {
        System.out.println("🛍️ Navigating to Shop page (Full Screen)...");
        if (shopNavButton != null) {
            navigateToPageFullScreen("/fxml/Shop.fxml", "Moosic - Shop", "/css/shopstyle.css", shopNavButton);
        }
    }

    @FXML private void handleOrderNav() {
        System.out.println("📦 Navigating to Order page (Full Screen)...");
    }
    
    private Image createSafeImage(String path) {
        if (path == null || path.trim().isEmpty()) return null;
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("Gagal menemukan resource gambar: " + path);
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error saat memuat gambar: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private ImageView createSafeImageView(String path, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView(createSafeImage(path));
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}