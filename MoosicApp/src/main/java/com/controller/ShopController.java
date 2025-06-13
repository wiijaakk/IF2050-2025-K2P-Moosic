package com.controller;

import com.model.AllProduct;
import com.model.ShopModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.scene.layout.BorderPane;

import org.w3c.dom.Text;

import javafx.scene.shape.SVGPath;

public class ShopController {

    @FXML private TilePane productGrid;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private ComboBox<String> variantComboBox;
    @FXML private TextField searchField;
    @FXML private HBox paginationContainer;
    @FXML private Button shopNavButton;
    @FXML private Button logoButton;

    private ShopModel model;
    private int currentPage = 0;
    private int currProductID;

    @FXML
    public void initialize() {
        model = new ShopModel();
        
        genreComboBox.getItems().addAll("Pop", "R&B", "Rock", "Jazz", "Indie", "EDM", "Music Genre");
        variantComboBox.getItems().addAll("Vinyl", "CD", "Cassette", "Product Variant");
        
        genreComboBox.setOnAction(e -> filterProducts());
        variantComboBox.setOnAction(e -> filterProducts());
        searchField.setOnAction(e -> filterProducts());

        updateProductDisplay();
        createPaginationControls();

        variantComboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int index = getIndex();
                    int lastIndex = variantComboBox.getItems().size() - 1;

                    if (index == 0) {
                        setStyle("-fx-background-radius: 15px 15px 0 0;");
                    } else if (index == lastIndex) {
                        setStyle("-fx-background-radius: 0 0 15px 15px;");
                    } else {
                        setStyle("-fx-background-radius: 0;");
                    }
                }
            }
        });

        genreComboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int index = getIndex();
                    int lastIndex = genreComboBox.getItems().size() - 1;

                    if (index == 0) {
                        setStyle("-fx-background-radius: 15px 15px 0 0;");
                    } else if (index == lastIndex) {
                        setStyle("-fx-background-radius: 0 0 15px 15px;");
                    } else {
                        setStyle("-fx-background-radius: 0;");
                    }
                }
            }
        });

        variantComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
            }
        });

        genreComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item);
            }
        });
        // if (shopNavButton != null) {
        //     shopNavButton.getStyleClass().add("active");
        // }
    }
    
    private void updateProductDisplay() {
        productGrid.getChildren().clear();
        List<AllProduct> productsOnPage = model.getProductsForPage(currentPage);

        for (AllProduct product : productsOnPage) {
            Node productNode = createProductItemNode(product);
            productGrid.getChildren().add(productNode);
        }
    }

    private void createPaginationControls() {
        paginationContainer.getChildren().clear();

        Button prevButton = new Button();
        ImageView prevIcon = new ImageView(new Image(getClass().getResourceAsStream("/image/before.png")));
        prevIcon.setFitHeight(10); 
        prevIcon.setFitWidth(10);
        prevButton.setGraphic(prevIcon);
        prevButton.getStyleClass().add("arrow-button");
        prevButton.setOnAction(this::handlePrevPage);
        if (currentPage == 0) {
            prevButton.setDisable(true);
        }
        paginationContainer.getChildren().add(prevButton);

        for (int i = 0; i < model.getTotalPages(); i++) {
            Button pageButton = new Button(String.valueOf(i + 1));
            final int pageIndex = i;
            pageButton.setOnAction(e -> handlePageChange(pageIndex));

            if (currentPage == i) {
                pageButton.getStyleClass().add("active");
            } else {
                pageButton.getStyleClass().add("inactive");
            }
            paginationContainer.getChildren().add(pageButton);
        }

        Button nextButton = new Button();
        ImageView nextIcon = new ImageView(new Image(getClass().getResourceAsStream("/image/next.png")));
        nextIcon.setFitHeight(10);
        nextIcon.setFitWidth(10);
        nextButton.setGraphic(nextIcon);
        nextButton.getStyleClass().add("arrow-button");
        nextButton.setOnAction(this::handleNextPage);
        if (currentPage >= model.getTotalPages() - 1) {
            nextButton.setDisable(true);
        }
        paginationContainer.getChildren().add(nextButton);

        if (shopNavButton != null) {
            shopNavButton.getStyleClass().add("active");
        }
    }
    
    private void handlePageChange(int pageIndex) {
        currentPage = pageIndex;
        updateProductDisplay();
        createPaginationControls();
    }
    
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 0) {
            currentPage--;
            updateProductDisplay();
            createPaginationControls();
        }
    }

    private void handleNextPage(ActionEvent event) {
        if (currentPage < model.getTotalPages() - 1) {
            currentPage++;
            updateProductDisplay();
            createPaginationControls();
        }
    }

    @FXML
    private void handleLogoClick(ActionEvent event) {
        searchField.clear();
        genreComboBox.setValue("Music Genre");
        variantComboBox.setValue("Product Variant");
        filterProducts();
    }

    @FXML
    private BorderPane mainContainer;

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
    
    private Node createProductItemNode(AllProduct product) {
        VBox itemBox = new VBox(8);
        itemBox.getStyleClass().add("product-item");
        // itemBox.setAlignment(Pos.CENTER_LEFT);

        itemBox.setOnMouseClicked(event -> navigateToProductDetail(product));

        // Region imagePlaceholder = new Region();
        // imagePlaceholder.setPrefSize(180, 180);
        // imagePlaceholder.setStyle("-fx-background-color: #f0f0f0;"); 
        StackPane imageContainer = new StackPane();
        Image image = new Image(product.getImage());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(190);
        imageView.setFitWidth(190);
        imageContainer.getChildren().add(imageView);

        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("product-name");

        HBox ratingBox = new HBox(4);
        ratingBox.setAlignment(Pos.CENTER_LEFT);

        Label starIcon = new Label("★");
        starIcon.getStyleClass().add("rating-star");

        Label ratingLabel = new Label(String.valueOf(product.getRating()));
        ratingLabel.getStyleClass().add("rating-text");

        Label separator = new Label("|");
        separator.getStyleClass().add("info-separator");

        Label soldLabel = new Label(String.format("%d pcs/month", product.getTerjual()));
        soldLabel.getStyleClass().add("sold-text"); 

        ratingBox.getChildren().addAll(starIcon, ratingLabel, separator, soldLabel);

        Label priceLabel = new Label(String.format("Rp%,.0f", product.getPrice()).replace(",", "."));
        priceLabel.getStyleClass().add("price-label");

        itemBox.getChildren().addAll(imageContainer, nameLabel, ratingBox, priceLabel);
        return itemBox;

    }

    private void filterProducts() {
        String selectedGenre = genreComboBox.getValue();
        String selectedVariant = variantComboBox.getValue();
        String userQuery = searchField.getText();
        model.applyFilterSearch(selectedGenre, selectedVariant, userQuery);

        currentPage = 0;
        updateProductDisplay();
        createPaginationControls();
    }

//     private void showAlert(Alert.AlertType alertType, String title, String message) {
//         Alert alert = new Alert(alertType);
//         alert.setTitle(title);
//         alert.setHeaderText(null);
//         alert.setContentText(message);
//         alert.showAndWait();
//     }
// }

    private void navigateToProductDetail(AllProduct product) {
        System.out.println("Anda mengklik produk: " + product.getName()); //kebutuhan debug brooo nanti jadi komen aja kalo udah implement
        currProductID = product.getId();
        if (!isValidProduct(product)) {
            System.out.println("Cannot navigate: invalid product");
            return;
        }
        currProductID = product.getId();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Product.fxml"));
            Parent root = loader.load();
            
            ProductController productController = loader.getController();
            productController.initData(currProductID);
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            
            // Save window state
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
            }
            
            stage.setScene(scene);
            stage.setTitle("Moose - Product Details");
            
            if (wasMaximized) {
                stage.setMaximized(false);
                stage.setMaximized(true); 
            } else {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
                stage.setX(currentX);
                stage.setY(currentY);
            }
            
            javafx.application.Platform.runLater(() -> {
                if (wasMaximized && !stage.isMaximized()) {
                    stage.setMaximized(true);
                }
                stage.toFront();
                stage.requestFocus();
                System.out.println("Final window state - Maximized: " + stage.isMaximized());
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", e.getMessage());
        }
    }

    public int getCurrProductID(){
        return currProductID;
    }

    public boolean isValidProduct(AllProduct product) { // FIXED: AllProduct
        if (product == null) return false;
        if (product.getName() == null || product.getName().trim().isEmpty()) return false;
        
        double priceValue = product.getPrice();
        if (priceValue < 0) return false;
        
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}