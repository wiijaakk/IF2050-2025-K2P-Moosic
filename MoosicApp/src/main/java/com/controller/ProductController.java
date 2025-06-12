package com.controller;

import com.database.ProductDAO;
import com.model.Product;
import com.model.ProductReview;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    @FXML private Button logoButton;
    @FXML private Button promotionNavButton;
    @FXML private Button cartNavButton;
    @FXML private Button shopNavButton;
    @FXML private Button orderNavButton;
    @FXML private Hyperlink backToShopLink;
    @FXML private Label productTitleLabel;
    @FXML private Label productPriceLabel;
    @FXML private Label productDescriptionLabel;
    @FXML private ImageView productImageView;
    @FXML private Label ratingSummaryLabel;
    @FXML private Label reviewCountLabel;
    @FXML private VBox reviewListContainer;
    @FXML private Button viewMoreButton;
    @FXML private Button viewLessButton;
    @FXML private Label quantityLabel;
    @FXML private Button checkoutButton;
    @FXML private VBox mainContainer;

    private Product currentProduct;
    private List<ProductReview> allProductReviews;
    private static final int INITIAL_REVIEW_COUNT = 2;
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quantityLabel.textProperty().bind(quantity.asString());
    }

    public void initData(int productId) {
        this.currentProduct = ProductDAO.getProductById(productId);

        if (this.currentProduct != null) {
            this.allProductReviews = this.currentProduct.getReviews();
            populateProductData(this.currentProduct);
            updateReviewSummary();
            displayReviews(INITIAL_REVIEW_COUNT);
        } else {
            productTitleLabel.setText("Product Not Found");
            reviewListContainer.getChildren().add(new Label("Could not load product details."));
            checkoutButton.setDisable(true);
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
        }
    }

    private void populateProductData(Product product) {
        productTitleLabel.setText(product.getName());
        productDescriptionLabel.setText(product.getDescription());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        productPriceLabel.setText(currencyFormatter.format(product.getPrice()));

        Image image = createSafeImage(product.getGambar());
        productImageView.setImage(image != null ? image : createSafeImage("/image/placeholder.png"));
    }

    private void updateReviewSummary() {
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            ratingSummaryLabel.setText("N/A");
            reviewCountLabel.setText("from 0 reviews");
            return;
        }
        double averageRating = allProductReviews.stream()
                .mapToDouble(ProductReview::getStar)
                .average()
                .orElse(0.0);
        ratingSummaryLabel.setText(String.format("%.1f / 5", averageRating));
        reviewCountLabel.setText("from " + allProductReviews.size() + " reviews");
    }

    private void displayReviews(int limit) {
        reviewListContainer.getChildren().clear();
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            reviewListContainer.getChildren().add(new Label("No reviews for this product yet."));
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
            return;
        }

        allProductReviews.stream().limit(limit).forEach(review -> {
            reviewListContainer.getChildren().add(createReviewCard(review));
        });

        boolean hasMoreReviews = allProductReviews.size() > INITIAL_REVIEW_COUNT;
        boolean isExpanded = limit >= allProductReviews.size();
        
        if (hasMoreReviews) {
            viewMoreButton.setVisible(!isExpanded);
            viewLessButton.setVisible(isExpanded);
        } else {
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
        }
    }

    // Metode untuk membuat kartu review
    private HBox createReviewCard(ProductReview review) {
        // ... (Implementasi tidak berubah, sudah baik)
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
        try {
            String fxmlPath = "/fxml/Checkout.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            CheckoutController checkoutController = loader.getController();
            checkoutController.setProductData(currentProduct, quantity.get());

            Stage stage = (Stage) checkoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            
            loadStylesheet(scene, fxmlPath);
            
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat halaman Checkout.fxml");
        }
    }

    @FXML private void handleViewMore() {
        if (allProductReviews != null) {
            displayReviews(allProductReviews.size());
        }
    }

    @FXML private void handleViewLess() {
        displayReviews(INITIAL_REVIEW_COUNT);
    }

    @FXML private void handleIncreaseQuantity() {
        quantity.set(quantity.get() + 1);
    }

    @FXML private void handleDecreaseQuantity() {
        if (quantity.get() > 1) {
            quantity.set(quantity.get() - 1);
        }
    }

    @FXML private void handleBackToShop() {
        navigateTo("/fxml/Shop.fxml", backToShopLink);
    }

    @FXML private void handleLogoClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Homepage.fxml"));
            Parent homePage = loader.load();

            // VBox approach - ganti content tanpa ganti scene
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(homePage);
            VBox.setVgrow(homePage, javafx.scene.layout.Priority.ALWAYS);

            // Load Homepage CSS
            try {
                String cssPath = getClass().getResource("/css/homepagestyle.css").toExternalForm();
                homePage.getStylesheets().add(cssPath);
                System.out.println("Loading Homepage CSS from: " + cssPath);
            } catch (Exception e) {
                System.out.println("Homepage CSS not found, using default styling");
            }

            System.out.println("Switched to Homepage (inline) - maintaining fullscreen");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open homepage: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML private void handlePromotionNav() {
        System.out.println("Navigasi ke Promotion page...");
        // navigateTo("/fxml/Promotion.fxml", promotionNavButton);
    }

    @FXML private void handleCartNav() {
        System.out.println("Navigasi ke Cart page...");
        // navigateTo("/fxml/Cart.fxml", cartNavButton);
    }

    @FXML private void handleShopNav() {
        navigateTo("/fxml/Shop.fxml", shopNavButton);
    }

    @FXML private void handleOrderNav() {
        System.out.println("Navigasi ke Order page...");
        // navigateTo("/fxml/Order.fxml", orderNavButton);
    }

private void navigateTo(String fxmlPath, Node currentNode) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) currentNode.getScene().getWindow();
            Scene scene = new Scene(root);

            loadStylesheet(scene, fxmlPath);
            
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal memuat halaman: " + fxmlPath);
        }
    }

    private void loadStylesheet(Scene scene, String fxmlPath) {
        String cssPath = fxmlPath.replace("/fxml/", "/css/").replace(".fxml", ".css");
        
        if (cssPath.contains("Shop")) {
            cssPath = "/css/shopstyle.css";
        } else if (cssPath.contains("LoginView")) {
            cssPath = "/css/loginstyle.css";
        }
        
        try {
            String cssUri = getClass().getResource(cssPath).toExternalForm();
            scene.getStylesheets().add(cssUri);
            System.out.println("Stylesheet berhasil dimuat: " + cssUri);
        } catch (NullPointerException e) {
            System.err.println("Stylesheet tidak ditemukan untuk: " + cssPath + ". Menggunakan style default.");
        }
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