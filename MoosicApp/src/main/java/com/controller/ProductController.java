package com.controller;

import com.database.ProductDAO;
import com.model.Product;
import com.model.ProductReview;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML ImageView logoImageView;
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
    @FXML Button decreaseQuantityButton;
    @FXML Label quantityLabel;
    @FXML Button increaseQuantityButton;
    @FXML Button checkoutButton;

    Product currentProduct;
    List<ProductReview> allProductReviews;
    static final int INITIAL_REVIEW_COUNT = 2;
    final IntegerProperty quantity = new SimpleIntegerProperty(1);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentProduct = ProductDAO.getProductById(1);
        if (this.currentProduct != null) {
            this.allProductReviews = this.currentProduct.getReviews();
            populateProductData(this.currentProduct);
            updateReviewSummary();
            displayReviews(INITIAL_REVIEW_COUNT);
        } else {
            productTitleLabel.setText("Product not found");
            reviewListContainer.getChildren().add(new Label("Could not load product details."));
            checkoutButton.setDisable(true); 
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
        }
        backToShopLink.setOnAction(event -> handleBackToShop());
        quantityLabel.textProperty().bind(quantity.asString());
        viewMoreButton.setOnAction(event -> handleViewMore());
        viewLessButton.setOnAction(event -> handleViewLess());
        increaseQuantityButton.setOnAction(event -> handleIncreaseQuantity());
        decreaseQuantityButton.setOnAction(event -> handleDecreaseQuantity());
        checkoutButton.setOnAction(event -> handleCheckout());
        registerNavigationHandlers();
    }
    
    void registerNavigationHandlers() {
        logoImageView.setOnMouseClicked(event -> handleLogoClick());
        promotionNavButton.setOnAction(event -> handlePromotionNav());
        cartNavButton.setOnAction(event -> handleCartNav());
        shopNavButton.setOnAction(event -> handleShopNav());
        orderNavButton.setOnAction(event -> handleOrderNav());
    }

    void populateProductData(Product product) {
        productTitleLabel.setText(product.getName());
        productDescriptionLabel.setText(product.getDescription());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("id", "ID"));
        productPriceLabel.setText(currencyFormatter.format(product.getPrice()));
        productImageView.setImage(createSafeImage(product.getGambar()));
        if (productImageView.getImage() == null) {
            productImageView.setImage(createSafeImage("/image/placeholder.png"));
        }
    }

    void updateReviewSummary() {
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            ratingSummaryLabel.setText("N/A");
            reviewCountLabel.setText("from 0 reviews");
            return;
        }
        double totalStars = 0;
        for (ProductReview review : allProductReviews) {
            totalStars += review.getStar();
        }
        double averageRating = totalStars / allProductReviews.size();
        ratingSummaryLabel.setText(String.format("%.1f / 5", averageRating));
        reviewCountLabel.setText("from " + allProductReviews.size() + " reviews");
    }

    void displayReviews(int limit) {
        reviewListContainer.getChildren().clear();
        if (allProductReviews == null || allProductReviews.isEmpty()) {
            reviewListContainer.getChildren().add(new Label("No reviews for this product yet."));
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
            return;
        }
        List<ProductReview> reviewsToDisplay = allProductReviews.stream().limit(limit).toList();
        for (ProductReview review : reviewsToDisplay) {
            reviewListContainer.getChildren().add(createReviewCard(review));
        }
        boolean isExpanded = (limit >= allProductReviews.size());
        boolean hasMoreReviewsThanInitial = allProductReviews.size() > INITIAL_REVIEW_COUNT;
        if (hasMoreReviewsThanInitial) {
            viewMoreButton.setVisible(!isExpanded);
            viewLessButton.setVisible(isExpanded);
        } else {
            viewMoreButton.setVisible(false);
            viewLessButton.setVisible(false);
        }
    }

    HBox createReviewCard(ProductReview review) {
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
    
    Image createSafeImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.err.println("Image path is null or empty.");
            return null;
        }
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("Failed to find image at path: " + path);
                return null;
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }
    
    ImageView createSafeImageView(String path, double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView();
        Image image = createSafeImage(path);
        if (image != null) {
            imageView.setImage(image);
            imageView.setFitWidth(fitWidth);
            imageView.setFitHeight(fitHeight);
            imageView.setPreserveRatio(true);
        }
        return imageView;
    }

    void handleCheckout() {
        int selectedQuantity = quantity.get();
        Product productToCheckout = currentProduct;
        System.out.println("Proceeding to checkout page...");
        System.out.println("Product: " + productToCheckout.getName());
        System.out.println("Quantity: " + selectedQuantity);
        // TODO: Implement logic to navigate to "Checkout" page.
        // 'productToCheckout' and 'selectedQuantity' will be passed to the next page's controller.
    }
    
    void handleViewMore() {
        displayReviews(allProductReviews.size());
    }
    
    void handleViewLess() {
        displayReviews(INITIAL_REVIEW_COUNT);
    }
    
    void handleIncreaseQuantity() {
        quantity.set(quantity.get() + 1);
    }

    void handleDecreaseQuantity() {
        if (quantity.get() > 1) {
            quantity.set(quantity.get() - 1);
        }
    }
    
    void handleBackToShop() {
        System.out.println("Back to Shop link clicked.");
        // TODO: Implement navigation back to the main Shop page
    }

    void handleLogoClick() {
        System.out.println("Logo clicked, navigating to Home page...");
        // TODO: Implement navigation to Home page
    }

    void handlePromotionNav() {
        System.out.println("Promotion navigation button clicked.");
        // TODO: Implement navigation to Promotion page
    }

    void handleCartNav() {
        System.out.println("Cart navigation button clicked.");
        // TODO: Implement navigation to Cart page
    }

    void handleShopNav() {
        System.out.println("Shop navigation button clicked.");
        // TODO: Implement navigation to Shop page
    }

    void handleOrderNav() {
        System.out.println("Order navigation button clicked.");
        // TODO: Implement navigation to Order page
    }
}