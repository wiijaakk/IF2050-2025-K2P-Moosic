package com.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final ObjectProperty<BigDecimal> price;
    private final StringProperty genre;
    private final StringProperty varian;
    private final StringProperty gambar;
    private final ListProperty<ProductReview> reviews;

    public Product(int id, String name, String description, BigDecimal price, String genre, String varian, String gambar, List<ProductReview> reviews) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleObjectProperty<>(price);
        this.genre = new SimpleStringProperty(genre);
        this.varian = new SimpleStringProperty(varian);
        this.gambar = new SimpleStringProperty(gambar);
        this.reviews = new SimpleListProperty<>(FXCollections.observableArrayList(reviews));
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDescription() { return description.get(); }
    public BigDecimal getPrice() { return price.get(); }
    public String getGenre() { return genre.get(); }
    public String getVarian() { return varian.get(); }
    public String getGambar() { return gambar.get(); }
    public ObservableList<ProductReview> getReviews() { return reviews.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<BigDecimal> priceProperty() { return price; }
    public StringProperty genreProperty() { return genre; }
    public StringProperty varianProperty() { return varian; }
    public StringProperty gambarProperty() { return gambar; }
    public ListProperty<ProductReview> reviewsProperty() { return reviews; }
}
