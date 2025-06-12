package com.database;

import com.model.Product;
import com.model.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDAOTest {

    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Koneksi ke database gagal, periksa konfigurasi .env");
        conn.setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.close();
        }
    }

    @Test
    @DisplayName("Test getProductById dengan ID yang valid")
    void testGetProductById_ValidId_ShouldReturnProduct() {
        int validProductId = 1;
        Product product = ProductDAO.getProductById(validProductId);
        assertNotNull(product, "Produk seharusnya tidak null untuk ID yang valid.");
        assertEquals(validProductId, product.getId(), "ID produk yang dikembalikan harus sama dengan yang diminta.");
        assertEquals("Midnights Vinyl – Taylor Swift", product.getName(), "Nama produk tidak sesuai dengan data dummy.");
        assertFalse(product.getReviews().isEmpty(), "Daftar ulasan untuk produk ini seharusnya tidak kosong.");
    }

    @Test
    @DisplayName("Test getProductById dengan ID yang tidak valid")
    void testGetProductById_InvalidId_ShouldReturnNull() {
        int invalidProductId = 9999;
        Product product = ProductDAO.getProductById(invalidProductId);
        assertNull(product, "Produk seharusnya null untuk ID yang tidak valid.");
    }

    @Test
    @DisplayName("Test getReviewsForProduct dengan ID produk yang memiliki review")
    void testGetReviewsForProduct_ValidId_ShouldReturnReviews() {
        int productIdWithReviews = 1;
        List<ProductReview> reviews = ProductDAO.getReviewsForProduct(productIdWithReviews);
        assertNotNull(reviews, "Daftar ulasan seharusnya tidak null.");
        assertFalse(reviews.isEmpty(), "Daftar ulasan seharusnya tidak kosong karena produk ini memiliki ulasan.");
    }

    @Test
    @DisplayName("Test getReviewsForProduct dengan ID produk yang tidak valid")
    void testGetReviewsForProduct_InvalidId_ShouldReturnEmptyList() {
        int invalidProductId = 9999;
        List<ProductReview> reviews = ProductDAO.getReviewsForProduct(invalidProductId);
        assertNotNull(reviews, "Metode seharusnya mengembalikan list kosong, bukan null.");
        assertTrue(reviews.isEmpty(), "Daftar ulasan seharusnya kosong untuk ID produk yang tidak valid.");
    }

    @Test
    @DisplayName("Test addReview harus menambah jumlah review di database")
    void testAddReview_ShouldIncreaseReviewCount() {
        int productId = 1;
        int userId = 2;
        List<ProductReview> initialReviews = ProductDAO.getReviewsForProduct(productId);
        int initialCount = initialReviews.size();

        ProductReview newReview = new ProductReview(
            99,
            productId,
            userId,
            "testuser", // username sementara
            "This is a test review.",
            5,
            OffsetDateTime.now()
        );
        ProductDAO.addReview(newReview);
        List<ProductReview> finalReviews = ProductDAO.getReviewsForProduct(productId);
        int finalCount = finalReviews.size();
        assertEquals(initialCount + 1, finalCount, "Jumlah review seharusnya bertambah satu setelah review baru ditambahkan.");
    }
}
