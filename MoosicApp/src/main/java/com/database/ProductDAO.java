package com.database;

import com.model.Product;
import com.model.ProductReview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE id_produk = ?";
        Product product = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("getProductById failed because database connection is null.");
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, productId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    List<ProductReview> reviews = getReviewsForProduct(productId); 
                    product = new Product(
                            rs.getInt("id_produk"),
                            rs.getString("nama_produk"),
                            rs.getString("deskripsi"), 
                            rs.getBigDecimal("harga"),
                            rs.getString("genre"),
                            rs.getString("varian"),
                            rs.getString("gambar"),
                            reviews
                    );
                } else {
                    System.err.println("No product found with id_produk = " + productId);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error fetching product by ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return product;
    }
    
    public static List<ProductReview> getReviewsForProduct(int productId) {
        String sql = "SELECT r.*, u.username FROM product_reviews r JOIN users u ON r.id_user = u.id_user WHERE r.id_produk = ? ORDER BY r.created_at DESC";
        List<ProductReview> reviews = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("getReviewsForProduct failed because database connection is null.");
                return reviews;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, productId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    reviews.add(new ProductReview(
                            rs.getInt("id_review"),
                            rs.getInt("id_produk"),
                            rs.getInt("id_user"),
                            rs.getString("username"),
                            rs.getString("review"),
                            rs.getInt("star"),
                            rs.getObject("created_at", OffsetDateTime.class)
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error fetching reviews for product " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return reviews;
    }

    public static void addReview(ProductReview review) {
        String sql = "INSERT INTO product_reviews (id_produk, id_user, review, star) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn == null) {
                System.err.println("addReview failed because database connection is null.");
                return;
            }

            try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, review.getId_produk());
                pstmt.setInt(2, review.getId_user()); 
                pstmt.setString(3, review.getReview());
                pstmt.setInt(4, review.getStar());
                pstmt.executeUpdate();
                System.out.println("Review added successfully.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error adding review: " + e.getMessage());
            e.printStackTrace();
        }
    }
}