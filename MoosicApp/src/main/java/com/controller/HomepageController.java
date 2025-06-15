package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Bounds;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.math.BigDecimal;
import com.model.AllProduct;
import com.database.HomepageDAO;
import com.database.DatabaseHomepage;

public class HomepageController {

    @FXML ScrollPane recommendedScrollPane;
    @FXML HBox recommendedProductsContainer;
    @FXML HBox topSellingProductsContainer;
    @FXML public TextField searchField;
    @FXML BorderPane mainContainer;
    @FXML StackPane searchBarContainer;

    Node lastHighlightedCard = null;
    int originalCardCount;
    static final int DUPLICATE_COUNT = 2;
    ObservableList<AllProduct> shoppingCart = FXCollections.observableArrayList();
    int currProductID;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (searchBarContainer != null && searchBarContainer.getParent() != null) {
                searchBarContainer.getParent().requestFocus();
            }
        });
        HomepageDAO.init();
        loadTopSellingProducts();
        loadRecommendedProducts();
        originalCardCount = recommendedProductsContainer.getChildren().size();
        if (originalCardCount > 0) {
            addInfiniteScrollDuplicates();
            recommendedScrollPane.hvalueProperty().addListener((obs, oldValue, newValue) -> {
                highlightCenterCard();
                handleInfiniteScrollLoop();
            });
            javafx.application.Platform.runLater(() -> {
                if (originalCardCount > 0 && recommendedProductsContainer.getChildren().size() > originalCardCount) {
                    Node firstOriginalCard = recommendedProductsContainer.getChildren().get(DUPLICATE_COUNT);
                    Bounds bounds = firstOriginalCard.getBoundsInParent();
                    double targetScrollX = bounds.getMinX() + (bounds.getWidth() / 2.0) - (recommendedScrollPane.getWidth() / 2.0);
                    double totalContentWidth = recommendedProductsContainer.getWidth();
                    double viewportWidth = recommendedScrollPane.getWidth();
                    if (totalContentWidth - viewportWidth > 0) {
                        double hvalue = targetScrollX / (totalContentWidth - viewportWidth);
                        hvalue = Math.max(0, Math.min(1, hvalue));
                        recommendedScrollPane.setHvalue(hvalue);
                    }
                }
                highlightCenterCard();
            });
        }
        
        shoppingCart.addListener((javafx.collections.ListChangeListener.Change<? extends AllProduct> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (AllProduct p : change.getAddedSubList()) {
                        System.out.println("Product added to cart: " + p.getName());
                    }
                }
                if (change.wasRemoved()) {
                    for (AllProduct p : change.getRemoved()) {
                        System.out.println("Product removed from cart: " + p.getName());
                    }
                }
            }
        });
    }

    private void loadRecommendedProducts() {
        if (recommendedProductsContainer != null) {
            recommendedProductsContainer.getChildren().clear();
        }

        try {
            List<AllProduct> products = HomepageDAO.getRecommendedProducts(10);
            
            if (products.isEmpty()) {
                System.out.println("No recommended products found in database. Loading fallback data...");
                loadFallbackRecommendedProducts();
                return;
            }

            for (AllProduct product : products) {
                VBox card = createProductCard(product);
                if (card != null && recommendedProductsContainer != null) {
                    recommendedProductsContainer.getChildren().add(card);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading recommended products from database: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Loading fallback data...");
            loadFallbackRecommendedProducts();
        }
    }

    private void loadFallbackRecommendedProducts() {
        try {
            List<AllProduct> products = new ArrayList<>();
            products.add(new AllProduct(1, "Midnights Vinyl - Taylor Swift", "Exclusive Midnights edition", 
                new BigDecimal("525000"), "Pop", "Vinyl", "/image/album/midnights.png", 4.5f, 15));
            products.add(new AllProduct(2, "25 Vinyl - Adele", "25 by Adele in Vinyl format", 
                new BigDecimal("253000"), "Pop", "Vinyl", "/image/album/25.png", 4.8f, 20));
            products.add(new AllProduct(3, "After Hours Vinyl - The Weeknd", "After Hours by The Weeknd", 
                new BigDecimal("243000"), "R&B", "Vinyl", "/image/album/afterhours.png", 4.7f, 18));
            products.add(new AllProduct(4, "Born to Die Vinyl - Lana Del Rey", "Born to Die by Lana Del Rey", 
                new BigDecimal("256000"), "Indie", "Vinyl", "/image/album/borntodie.png", 4.9f, 25));
            products.add(new AllProduct(5, "Come Away with Me Vinyl - Norah Jones", "Come Away with Me by Norah Jones", 
                new BigDecimal("252000"), "Jazz", "Vinyl", "/image/album/comeaway.png", 4.6f, 17));

            for (AllProduct product : products) {
                VBox card = createProductCard(product);
                if (card != null && recommendedProductsContainer != null) {
                    recommendedProductsContainer.getChildren().add(card);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading fallback recommended products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createProductCard(AllProduct product) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_CENTER);
        card.setSpacing(5);
        card.setPadding(new Insets(15));
        
        card.setPrefWidth(280.0);
        card.setPrefHeight(380.0);

        card.getStyleClass().add("product-card");
        card.getStyleClass().add("product-card-white");

        ImageView imageView = new ImageView();
        try {
            String imagePath = product.getImage();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                if (!image.isError()) {
                    imageView.setImage(image);
                }
            } else {
                throw new Exception("Image path is null or empty");
            }
        } catch (Exception e) {
            System.err.println("Error loading image for product: " + product.getName() + " - " + e.getMessage());
        }
        
        imageView.setFitHeight(200.0);
        imageView.setFitWidth(200.0);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("product-image");

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label priceLabel = new Label(formatPrice(product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        card.getChildren().addAll(imageView, nameLabel, priceLabel);
        
        card.setOnMouseClicked(event -> handleRecommendedProductClick(product, card));
        card.getStyleClass().add("clickable-card");
        
        final Timeline[] currentAnimation = {null};
        
        card.setOnMouseEntered(event -> {
            if (currentAnimation[0] != null) {
                currentAnimation[0].stop();
            }
            
            if (card.getStyleClass().contains("highlighted-card")) {
                Timeline hoverInTimeline = new Timeline();
            
                KeyValue translateY = new KeyValue(
                    card.translateYProperty(), 
                    -20, 
                    Interpolator.EASE_OUT
                );
                
                KeyValue scaleX = new KeyValue(
                    card.scaleXProperty(), 
                    1.05, 
                    Interpolator.EASE_OUT
                );
                KeyValue scaleY = new KeyValue(
                    card.scaleYProperty(), 
                    1.05, 
                    Interpolator.EASE_OUT
                );
                
                javafx.scene.effect.DropShadow enhancedGlow = new javafx.scene.effect.DropShadow();
                enhancedGlow.setColor(javafx.scene.paint.Color.rgb(77, 113, 17, 0.7));
                enhancedGlow.setRadius(30);
                enhancedGlow.setOffsetY(10);
                
                KeyValue shadowEffect = new KeyValue(
                    card.effectProperty(), 
                    enhancedGlow, 
                    Interpolator.EASE_OUT
                );
                
                KeyFrame hoverFrame = new KeyFrame(
                    Duration.millis(250), 
                    translateY, scaleX, scaleY, shadowEffect
                );
                
                hoverInTimeline.getKeyFrames().add(hoverFrame);
                currentAnimation[0] = hoverInTimeline;
                hoverInTimeline.play();
            }
        });
        
        card.setOnMouseExited(event -> {
            if (currentAnimation[0] != null) {
                currentAnimation[0].stop();
            }
            
            if (card.getStyleClass().contains("highlighted-card")) {
                Timeline hoverOutTimeline = new Timeline();
                
                KeyValue returnTranslateY = new KeyValue(
                    card.translateYProperty(), 
                    -10, 
                    Interpolator.EASE_OUT
                );
                
                KeyValue returnScaleX = new KeyValue(
                    card.scaleXProperty(), 
                    1.05, 
                    Interpolator.EASE_OUT
                );
                KeyValue returnScaleY = new KeyValue(
                    card.scaleYProperty(), 
                    1.05, 
                    Interpolator.EASE_OUT
                );
                
                javafx.scene.effect.DropShadow normalGlow = new javafx.scene.effect.DropShadow();
                normalGlow.setColor(javafx.scene.paint.Color.rgb(77, 113, 17, 0.4));
                normalGlow.setRadius(20);
                normalGlow.setOffsetY(5);
                
                KeyValue returnShadow = new KeyValue(
                    card.effectProperty(), 
                    normalGlow, 
                    Interpolator.EASE_OUT
                );
                
                KeyFrame returnFrame = new KeyFrame(
                    Duration.millis(250), 
                    returnTranslateY, returnScaleX, returnScaleY, returnShadow
                );
                
                hoverOutTimeline.getKeyFrames().add(returnFrame);
                currentAnimation[0] = hoverOutTimeline;
                hoverOutTimeline.play();
            }
        });
        
        return card;
    }

    private String formatPrice(double price) {
        return String.format("Rp %.0f", price);
    }

    private void setHighlightedWithAnimation(Node card, boolean highlighted) {
        if (card == null) return;
        
        Timeline timeline = new Timeline();
        
        if (highlighted) {
            if (!card.getStyleClass().contains("highlighted-card")) {
                card.getStyleClass().add("highlighted-card");
            }
            
            KeyValue highlightTranslateY = new KeyValue(
                card.translateYProperty(), 
                -10, 
                Interpolator.EASE_OUT
            );
            
            KeyValue highlightScaleX = new KeyValue(
                card.scaleXProperty(), 
                1.05, 
                Interpolator.EASE_OUT
            );
            
            KeyValue highlightScaleY = new KeyValue(
                card.scaleYProperty(), 
                1.05, 
                Interpolator.EASE_OUT
            );
            
            javafx.scene.effect.DropShadow highlightGlow = new javafx.scene.effect.DropShadow();
            highlightGlow.setColor(javafx.scene.paint.Color.rgb(77, 113, 17, 0.4));
            highlightGlow.setRadius(20);
            highlightGlow.setOffsetY(5);
            
            KeyValue highlightShadow = new KeyValue(
                card.effectProperty(), 
                highlightGlow, 
                Interpolator.EASE_OUT
            );
            
            KeyFrame highlightFrame = new KeyFrame(
                Duration.millis(400), 
                highlightTranslateY, highlightScaleX, highlightScaleY, highlightShadow
            );
            
            timeline.getKeyFrames().add(highlightFrame);
            
        } else {
            card.getStyleClass().remove("highlighted-card");
            
            KeyValue normalTranslateY = new KeyValue(
                card.translateYProperty(), 
                0, 
                Interpolator.EASE_OUT
            );
            
            KeyValue normalScaleX = new KeyValue(
                card.scaleXProperty(), 
                1.0, 
                Interpolator.EASE_OUT
            );
            
            KeyValue normalScaleY = new KeyValue(
                card.scaleYProperty(), 
                1.0, 
                Interpolator.EASE_OUT
            );
            
            javafx.scene.effect.DropShadow normalShadow = new javafx.scene.effect.DropShadow();
            normalShadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.1));
            normalShadow.setRadius(10);
            normalShadow.setOffsetY(2);
            
            KeyValue normalShadowEffect = new KeyValue(
                card.effectProperty(), 
                normalShadow, 
                Interpolator.EASE_OUT
            );
            
            KeyFrame normalFrame = new KeyFrame(
                Duration.millis(300), 
                normalTranslateY, normalScaleX, normalScaleY, normalShadowEffect
            );
            
            timeline.getKeyFrames().add(normalFrame);
        }
        
        timeline.play();
    }

    private void highlightCenterCard() {
        if (recommendedScrollPane == null || recommendedProductsContainer == null) {
            return;
        }

        double viewportWidth = recommendedScrollPane.getWidth();
        double scrollX = recommendedScrollPane.getHvalue() * (recommendedProductsContainer.getWidth() - viewportWidth);

        Node currentCenterCard = null;
        double minDistanceToCenter = Double.MAX_VALUE;

        ObservableList<Node> allCards = recommendedProductsContainer.getChildren();
        int startIndex = DUPLICATE_COUNT;
        int endIndex = DUPLICATE_COUNT + originalCardCount;

        if (startIndex >= allCards.size() || endIndex > allCards.size()) {
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            Node card = allCards.get(i);
            Bounds bounds = card.getBoundsInParent();

            double cardCenterXInViewport = bounds.getMinX() + bounds.getWidth() / 2.0 - scrollX;
            double distance = Math.abs(cardCenterXInViewport - viewportWidth / 2.0);

            if (distance < minDistanceToCenter) {
                minDistanceToCenter = distance;
                currentCenterCard = card;
            }
        }

        if (lastHighlightedCard != null && lastHighlightedCard != currentCenterCard) {
            setHighlightedWithAnimation(lastHighlightedCard, false);
        }

        if (currentCenterCard != null && !currentCenterCard.getStyleClass().contains("highlighted-card")) {
            setHighlightedWithAnimation(currentCenterCard, true);
            lastHighlightedCard = currentCenterCard;
        }
    }

    public void handleTopSellingProductClick(AllProduct product) {
        System.out.println("Top selling product clicked: " + product.getName());
        navigateToProductDetail(product);
    }
    
    public void handleRecommendedProductClick(AllProduct product, VBox card) {
        if (card.getStyleClass().contains("highlighted-card")) {
            System.out.println("Highlighted recommended product clicked: " + product.getName());
            navigateToProductDetail(product);
        } else {
            System.out.println("Non-highlighted card clicked, ignoring...");
        }
    }
    
    private void navigateToProductDetail(AllProduct product) {
        System.out.println("Anda mengklik produk: " + product.getName());
        currProductID = product.getId();
        
        if (!isValidProduct(product)) {
            System.out.println("Cannot navigate: invalid product");
            return;
        }
        
        currProductID = product.getId();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Product.fxml"));
            Parent productPage = loader.load();
            
            ProductController productController = loader.getController();
            productController.initData(currProductID);
            
            mainContainer.setTop(null);
            mainContainer.setBottom(null);
            mainContainer.setCenter(null);
            mainContainer.setCenter(productPage);
            
            try {
                String cssPath = getClass().getResource("/css/product.css").toExternalForm();
                productPage.getStylesheets().clear();
                productPage.getStylesheets().add(cssPath);
                System.out.println("Loading Product CSS from: " + cssPath);
            } catch (Exception e) {
                System.out.println("Product CSS not found, using default styling");
            }
            
            System.out.println("Switched to Product Detail Page (inline) - Product ID: " + currProductID);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                "Could not open product detail page: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error",
                "An unexpected error occurred while navigating to product detail: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogoClick() {
        System.out.println("Logo clicked - navigating to home");
    }
    
    @FXML
    public void handleSearchAction() {
        if (searchField != null) {
            String searchQuery = searchField.getText().trim();
        
            if (!searchQuery.isEmpty()) {
                System.out.println("Search triggered with query: '" + searchQuery + "'");
                navigateToSearchResults(searchQuery);
            } else {
                System.out.println("Empty search query, ignoring...");
            }
        }
    }
    
    public void navigateToSearchResults(String searchQuery) {
        if (!isValidSearchQuery(searchQuery)) {
            System.out.println("Invalid search query: '" + searchQuery + "'");
            return;
        }
        
        String formattedQuery = formatSearchQuery(searchQuery);
        System.out.println("Navigating to search results page");
        System.out.println("Search Query: '" + formattedQuery + "'");
        System.out.println("Query Length: " + formattedQuery.length() + " characters");
    }
    
    @FXML
    private void handlePromotionClick() {
        System.out.println("Promotion clicked - navigating to promotions page");
    }
    
    @FXML
    private void handleCartClick() {
        System.out.println("Cart clicked - navigating to cart page");
    }
    
    @FXML
    private void handleShopClick() {
        System.out.println("Shop clicked - navigating to shop page");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Shop.fxml"));
            Parent homePage = loader.load();

            if (mainContainer != null) {
                mainContainer.setTop(null);
                mainContainer.setBottom(null);
                mainContainer.setCenter(null);
                mainContainer.setCenter(homePage);
            }

            try {
                String cssPath = getClass().getResource("/css/shopstyle.css").toExternalForm();
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
    private void handleOrderClick() {
        System.out.println("Order clicked - navigating to order page");
    }

    private void handleAddToCart(Button button) {
        AllProduct product = (AllProduct) button.getUserData();
        if (product != null) {
            shoppingCart.add(product);
            System.out.println("Added " + product.getName() + " to cart!");
        }
    }

    private void loadTopSellingProducts() {
        if (topSellingProductsContainer != null) {
            topSellingProductsContainer.getChildren().clear();
        }

        try {
            List<AllProduct> topProducts = HomepageDAO.getTopSellingProducts(3);
            
            if (topProducts.isEmpty()) {
                loadFallbackTopSellingProducts();
                return;
            }

            int index = 1;
            for (AllProduct product : topProducts) {
                if (topSellingProductsContainer != null) {
                    topSellingProductsContainer.getChildren().add(createTopSellingCard(product, index));
                }
                index++;
            }
            
        } catch (Exception e) {
            loadFallbackTopSellingProducts();
        }
    }

    private void loadFallbackTopSellingProducts() {
        List<AllProduct> topProducts = new ArrayList<>();
        topProducts.add(new AllProduct(1, "Midnights Vinyl - Taylor Swift", "Exclusive Midnights edition", 
            new BigDecimal("525000"), "Pop", "Vinyl", "/image/album/midnights.png", 4.5f, 15));
        topProducts.add(new AllProduct(2, "25 Vinyl - Adele", "25 by Adele in Vinyl format", 
            new BigDecimal("253000"), "Pop", "Vinyl", "/image/album/25.png", 4.8f, 20));
        topProducts.add(new AllProduct(3, "After Hours Vinyl - The Weeknd", "After Hours by The Weeknd", 
            new BigDecimal("243000"), "R&B", "Vinyl", "/image/album/afterhours.png", 4.9f, 25));

        int index = 1;
        for (AllProduct product : topProducts) {
            if (topSellingProductsContainer != null) {
                topSellingProductsContainer.getChildren().add(createTopSellingCard(product, index));
            }
            index++;
        }
    }

    private StackPane createTopSellingCard(AllProduct product, int displayIndex) {
        StackPane stackPane = new StackPane();

        VBox cardVBox = new VBox();
        cardVBox.setAlignment(Pos.BOTTOM_CENTER);
        cardVBox.setSpacing(10.0);
        cardVBox.setPadding(new Insets(15));
        cardVBox.getStyleClass().add("product-card");

        String colorClass = "product-card-grey";
        double prefWidth = 250.0;
        double prefHeight = 250.0;
        double imageHeight = 150.0;

        if (displayIndex == 1) {
            colorClass = "product-card-yellow";
            prefWidth = 250.0;
            prefHeight = 300.0;
            imageHeight = 180.0;
        } else if (displayIndex == 2) {
            colorClass = "product-card-grey";
            prefWidth = 250.0;
            prefHeight = 300.0;
            imageHeight = 180.0;
        } else if (displayIndex == 3) {
            colorClass = "product-card-brown";
            prefWidth = 250.0;
            prefHeight = 300.0;
            imageHeight = 180.0;
        }
        
        cardVBox.getStyleClass().add(colorClass);
        cardVBox.setPrefWidth(prefWidth);
        cardVBox.setPrefHeight(prefHeight);

        ImageView imageView = new ImageView();
        try {
            String imagePath = product.getImage();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                var inputStream = getClass().getResourceAsStream(imagePath);
                if (inputStream != null) {
                    Image image = new Image(inputStream);
                    if (!image.isError()) {
                        imageView.setImage(image);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading top selling image: " + product.getImage() + " - " + e.getMessage());
        }
        
        imageView.setFitHeight(imageHeight);
        imageView.setFitWidth(imageHeight);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("product-image");

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label salesLabel = new Label(String.format("%.1f ⭐ | %d sold", product.getRating(), product.getTerjual()));
        salesLabel.getStyleClass().add("product-sales");

        Label priceLabel = new Label(formatPrice(product.getPrice()));
        priceLabel.getStyleClass().add("product-price");

        cardVBox.getChildren().addAll(imageView, nameLabel, salesLabel, priceLabel);
        stackPane.getChildren().add(cardVBox);

        stackPane.setOnMouseClicked(event -> handleTopSellingProductClick(product));
        stackPane.getStyleClass().add("clickable-card");

        Label rankingLabel = new Label(String.valueOf(displayIndex));
        
        rankingLabel.setStyle(
            "-fx-min-width: 32px; " +
            "-fx-min-height: 32px; " +
            "-fx-max-width: 32px; " +
            "-fx-max-height: 32px; " +
            "-fx-background-radius: 50%; " +
            "-fx-border-radius: 50%; " +
            "-fx-border-width: 2px; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-alignment: center; " +
            "-fx-alignment: center; " +
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 3, 3); " +
            getBadgeColor(displayIndex)
        );
        
        rankingLabel.getStyleClass().addAll("ranking-badge", getRankingClass(displayIndex));
        
        StackPane.setAlignment(rankingLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(rankingLabel, new Insets(-16.0, -16.0, 0, 0));
        
        stackPane.getChildren().add(rankingLabel);

        return stackPane;
    }
    
    private String getBadgeColor(int ranking) {
        return switch (ranking) {
            case 1 -> "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-color: #FFD700;";
            case 2 -> "-fx-background-color: linear-gradient(to bottom, #C0C0C0, #A8A8A8); -fx-border-color: #C0C0C0;";
            case 3 -> "-fx-background-color: linear-gradient(to bottom, #CD7F32, #A0522D); -fx-border-color: #CD7F32;";
            default -> "-fx-background-color: linear-gradient(to bottom, #6C757D, #495057); -fx-border-color: #6C757D;";
        };
    }
    
    private String getRankingClass(int ranking) {
        return switch (ranking) {
            case 1 -> "ranking-gold";
            case 2 -> "ranking-silver";
            case 3 -> "ranking-bronze";
            default -> "ranking-default";
        };
    }

    public boolean isValidSearchQuery(String query) {
        if (query == null) return false;
        if (query.trim().isEmpty()) return false;
        if (query.length() > 100) return false;
        return true;
    }

    public String formatSearchQuery(String query) {
        if (query == null) return "";
        return query.trim().toLowerCase();
    }

    public boolean isValidProduct(AllProduct product) {
        if (product == null) return false;
        if (product.getName() == null || product.getName().trim().isEmpty()) return false;
        
        double price = product.getPrice();
        if (price < 0) return false;
        
        return true;
    }

    public ObservableList<AllProduct> getShoppingCart() {
        return this.shoppingCart;
    }

    public double calculateCartTotalAsDouble() {
        return shoppingCart.stream()
            .mapToDouble(AllProduct::getPrice)
            .sum();
    }

    public BigDecimal calculateCartTotal() {
        double total = shoppingCart.stream()
            .mapToDouble(AllProduct::getPrice)
            .sum();
        return BigDecimal.valueOf(total);
    }

    public int getCartItemCount() {
        return shoppingCart.size();
    }

    public void clearCart() {
        shoppingCart.clear();
    }

    public boolean isProductInCart(AllProduct product) {
        return shoppingCart.contains(product);
    }

    public int getCartProductCountByGenre(String genre) {
        return (int) shoppingCart.stream()
            .filter(product -> genre.equals(product.getGenre()))
            .count();
    }

    private Node findCenterCard(ObservableList<Node> cards, double hvalue, double totalContentWidth, double viewportWidth) {
        Node centerCard = null;
        double minDistanceToCenter = Double.MAX_VALUE;
        double currentScrollPosition = hvalue * (totalContentWidth - viewportWidth);

        for (Node card : cards) {
            Bounds bounds = card.getBoundsInParent();
            double cardCenterX = bounds.getMinX() + bounds.getWidth() / 2.0;
            double distance = Math.abs(cardCenterX - (currentScrollPosition + viewportWidth / 2.0));

            if (distance < minDistanceToCenter) {
                minDistanceToCenter = distance;
                centerCard = card;
            }
        }
        return centerCard;
    }

    private void animateScroll(double targetHvalue) {
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(recommendedScrollPane.hvalueProperty(), targetHvalue, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    private void addInfiniteScrollDuplicates() {
        if (originalCardCount == 0) {
            System.err.println("Cannot add infinite scroll duplicates: no original cards");
            return;
        }
        
        if (originalCardCount < DUPLICATE_COUNT) {
            System.err.println("Not enough cards for infinite scroll (need at least " + DUPLICATE_COUNT + ", got " + originalCardCount + ")");
            return;
        }

        if (recommendedProductsContainer == null) {
            System.err.println("Cannot add infinite scroll duplicates: container is null");
            return;
        }

        ObservableList<Node> cards = recommendedProductsContainer.getChildren();
        if (cards.isEmpty()) {
            System.err.println("Cannot add infinite scroll duplicates: cards list is empty");
            return;
        }

        List<Node> nodesToPrepend = new ArrayList<>();
        List<Node> nodesToAppend = new ArrayList<>();

        try {
            for (int i = originalCardCount - DUPLICATE_COUNT; i < originalCardCount; i++) {
                if (i >= 0 && i < cards.size()) {
                    Node duplicate = createDeepCopy(cards.get(i));
                    if (duplicate != null) {
                        nodesToPrepend.add(duplicate);
                    }
                }
            }

            for (int i = 0; i < DUPLICATE_COUNT && i < originalCardCount; i++) {
                if (i < cards.size()) {
                    Node duplicate = createDeepCopy(cards.get(i));
                    if (duplicate != null) {
                        nodesToAppend.add(duplicate);
                    }
                }
            }

            cards.addAll(0, nodesToPrepend);
            cards.addAll(nodesToAppend);
            
        } catch (Exception e) {
            System.err.println("Error adding infinite scroll duplicates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Node createDeepCopy(Node originalNode) {
        if (originalNode == null) {
            System.err.println("Cannot create deep copy: original node is null");
            return null;
        }
        
        if (originalNode instanceof VBox) {
            VBox originalVBox = (VBox) originalNode;
            VBox newVBox = new VBox();
            
            try {
                newVBox.setAlignment(originalVBox.getAlignment());
                newVBox.setSpacing(originalVBox.getSpacing());
                newVBox.setPadding(originalVBox.getPadding());
                newVBox.setPrefWidth(originalVBox.getPrefWidth());
                newVBox.setPrefHeight(originalVBox.getPrefHeight());
                newVBox.getStyleClass().addAll(originalVBox.getStyleClass());

                for (Node child : originalVBox.getChildren()) {
                    if (child instanceof ImageView) {
                        ImageView originalImageView = (ImageView) child;
                        ImageView newImageView = new ImageView(originalImageView.getImage());
                        newImageView.setFitWidth(originalImageView.getFitWidth());
                        newImageView.setFitHeight(originalImageView.getFitHeight());
                        newImageView.setPreserveRatio(originalImageView.isPreserveRatio());
                        newImageView.getStyleClass().addAll(originalImageView.getStyleClass());
                        newVBox.getChildren().add(newImageView);
                    } else if (child instanceof Label) {
                        Label originalLabel = (Label) child;
                        Label newLabel = new Label(originalLabel.getText());
                        newLabel.getStyleClass().addAll(originalLabel.getStyleClass());
                        newLabel.setTextAlignment(originalLabel.getTextAlignment());
                        newVBox.getChildren().add(newLabel);
                    } else if (child instanceof Button) {
                        Button originalButton = (Button) child;
                        Button newButton = new Button(originalButton.getText());
                        newButton.getStyleClass().addAll(originalButton.getStyleClass());
                        newButton.setUserData(originalButton.getUserData());
                        newButton.setOnAction(originalButton.getOnAction());
                        newVBox.getChildren().add(newButton);
                    }
                }
                return newVBox;
            } catch (Exception e) {
                System.err.println("Error creating deep copy of VBox: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        
        System.err.println("Cannot create deep copy: unsupported node type " + originalNode.getClass().getSimpleName());
        return null;
    }

    private void handleInfiniteScrollLoop() {
        if (recommendedScrollPane == null || recommendedProductsContainer == null) {
            return;
        }

        double currentHvalue = recommendedScrollPane.getHvalue();
        double totalContentWidth = recommendedProductsContainer.getWidth();
        double viewportWidth = recommendedScrollPane.getWidth();

        if (totalContentWidth - viewportWidth <= 0 || originalCardCount == 0) return;

        if (recommendedProductsContainer.getChildren().isEmpty()) return;

        int firstOriginalIndex = DUPLICATE_COUNT;
        int lastOriginalIndex = DUPLICATE_COUNT + originalCardCount - 1;

        if (lastOriginalIndex >= recommendedProductsContainer.getChildren().size() || firstOriginalIndex < 0) {
             return;
        }

        Bounds firstOriginalBounds = recommendedProductsContainer.getChildren().get(firstOriginalIndex).getBoundsInParent();
        Bounds lastOriginalBounds = recommendedProductsContainer.getChildren().get(lastOriginalIndex).getBoundsInParent();

        double hvalueStartOriginal = (firstOriginalBounds.getMinX() + (firstOriginalBounds.getWidth() / 2.0) - (viewportWidth / 2.0)) / (totalContentWidth - viewportWidth);
        double hvalueEndOriginal = (lastOriginalBounds.getMinX() + (lastOriginalBounds.getWidth() / 2.0) - (viewportWidth / 2.0)) / (totalContentWidth - viewportWidth);

        hvalueStartOriginal = Math.max(0, Math.min(1, hvalueStartOriginal));
        hvalueEndOriginal = Math.max(0, Math.min(1, hvalueEndOriginal));

        double epsilon = 0.001;

        if (currentHvalue > hvalueEndOriginal + epsilon) {
            recommendedScrollPane.setHvalue(hvalueStartOriginal);
        }
        else if (currentHvalue < hvalueStartOriginal - epsilon) {
            recommendedScrollPane.setHvalue(hvalueEndOriginal);
        }
    }

    @FXML
    private void scrollLeft() {
        if (recommendedScrollPane == null || recommendedProductsContainer == null) {
            return;
        }

        double currentHvalue = recommendedScrollPane.getHvalue();
        ObservableList<Node> allCards = recommendedProductsContainer.getChildren();
        double viewportWidth = recommendedScrollPane.getWidth();
        double totalContentWidth = recommendedProductsContainer.getWidth();

        if (allCards.isEmpty() || totalContentWidth == 0 || viewportWidth == 0) return;

        Node currentCenterCard = findCenterCard(allCards, currentHvalue, totalContentWidth, viewportWidth);

        if (currentCenterCard != null) {
            int currentCardIndex = allCards.indexOf(currentCenterCard);
            int targetIndex = currentCardIndex - 1;

            if (targetIndex < DUPLICATE_COUNT) {
                targetIndex = DUPLICATE_COUNT + originalCardCount - 1;
            }

            if (targetIndex >= 0 && targetIndex < allCards.size()) {
                Node targetCard = allCards.get(targetIndex);
                Bounds targetBounds = targetCard.getBoundsInParent();

                double targetScrollX = targetBounds.getMinX() + (targetBounds.getWidth() / 2.0) - (viewportWidth / 2.0);
                double targetHvalue = targetScrollX / (totalContentWidth - viewportWidth);
                animateScroll(targetHvalue);
            }
        }
    }

    @FXML
    private void scrollRight() {
        if (recommendedScrollPane == null || recommendedProductsContainer == null) {
            return;
        }

        double currentHvalue = recommendedScrollPane.getHvalue();
        ObservableList<Node> allCards = recommendedProductsContainer.getChildren();
        double viewportWidth = recommendedScrollPane.getWidth();
        double totalContentWidth = recommendedProductsContainer.getWidth();

        if (allCards.isEmpty() || totalContentWidth == 0 || viewportWidth == 0) return;

        Node currentCenterCard = findCenterCard(allCards, currentHvalue, totalContentWidth, viewportWidth);

        if (currentCenterCard != null) {
            int currentCardIndex = allCards.indexOf(currentCenterCard);
            int targetIndex = currentCardIndex + 1;

            if (targetIndex >= DUPLICATE_COUNT + originalCardCount) {
                targetIndex = DUPLICATE_COUNT;
            }

            if (targetIndex >= 0 && targetIndex < allCards.size()) {
                Node targetCard = allCards.get(targetIndex);
                Bounds targetBounds = targetCard.getBoundsInParent();

                double targetScrollX = targetBounds.getMinX() + (targetBounds.getWidth() / 2.0) - (viewportWidth / 2.0);
                double targetHvalue = targetScrollX / (totalContentWidth - viewportWidth);
                animateScroll(targetHvalue);
            }
        }
    }

    public int getCurrProductID(){
        return currProductID;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}