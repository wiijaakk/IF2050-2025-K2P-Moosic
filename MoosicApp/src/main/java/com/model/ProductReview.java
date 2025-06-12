package com.model;

import javafx.beans.property.*;
import java.time.OffsetDateTime;

public class ProductReview {
    private final IntegerProperty id_review;
    private final IntegerProperty id_produk;
    private final IntegerProperty id_user;
    private final StringProperty customerUsername;
    private final StringProperty review;
    private final IntegerProperty star;
    private final ObjectProperty<OffsetDateTime> createdAt;

    public ProductReview(int id_review, int id_produk, int id_user, String customerUsername, String review, int star, OffsetDateTime createdAt) {
        this.id_review = new SimpleIntegerProperty(id_review);
        this.id_produk = new SimpleIntegerProperty(id_produk);
        this.id_user = new SimpleIntegerProperty(id_user);
        this.customerUsername = new SimpleStringProperty(customerUsername);
        this.review = new SimpleStringProperty(review);
        this.star = new SimpleIntegerProperty(star);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    public int getId_review() { return id_review.get(); }
    public int getId_produk() { return id_produk.get(); }
    public int getId_user() { return id_user.get(); }
    public String getCustomerUsername() { return customerUsername.get(); }
    public String getReview() { return review.get(); }
    public int getStar() { return star.get(); }
    public OffsetDateTime getCreatedAt() { return createdAt.get(); }

    public IntegerProperty id_reviewProperty() { return id_review; }
    public StringProperty customerUsernameProperty() { return customerUsername; }
    public StringProperty reviewProperty() { return review; }
    public IntegerProperty starProperty() { return star; }
    public ObjectProperty<OffsetDateTime> createdAtProperty() { return createdAt; }
}