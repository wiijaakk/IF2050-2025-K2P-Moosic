package com.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import com.model.AllProduct; 

public class HomepageDAO {

    public static void init() {
        try (Connection conn = DatabaseHomepage.getConnection(); 
             Statement stmt = conn.createStatement()) {

            if (conn == null) {
                System.err.println("Failed to initialize database: No connection established.");
                return;
            }

            String sql = "CREATE TABLE IF NOT EXISTS transaction_details (detail_id SERIAL PRIMARY KEY,"
                        +"id_transaksi INT NOT NULL,"
                        +"id_produk INT NOT NULL,"
                        +"quantity INT NOT NULL DEFAULT 1,"
                        +"price_at_transaction DECIMAL(15, 2) NOT NULL,"
                        +"FOREIGN KEY (id_transaksi) REFERENCES transactions(id_transaksi) ON DELETE CASCADE,"
                        +"FOREIGN KEY (id_produk) REFERENCES products(id_produk) ON DELETE RESTRICT);";
            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<AllProduct> getTopSellingProducts(int limit) {
        List<AllProduct> topSellingProducts = new ArrayList<>();
        
        String sql = """
            SELECT 
                p.id_produk,
                p.nama_produk,
                p.deskripsi,
                p.harga,
                p.genre,
                p.varian,
                p.gambar,
                COALESCE(SUM(td.quantity), 0) as total_sold,
                COALESCE(AVG(pr.star), 0) as avg_rating,
                COALESCE(COUNT(DISTINCT pr.id_review), 0) as review_count
            FROM products p
            LEFT JOIN transaction_details td ON p.id_produk = td.id_produk
            LEFT JOIN transactions t ON td.id_transaksi = t.id_transaksi 
                AND t.payment_status IN ('Completed', 'Success', 'Paid')
            LEFT JOIN product_reviews pr ON p.id_produk = pr.id_produk
            GROUP BY p.id_produk, p.nama_produk, p.deskripsi, p.harga, p.genre, p.varian, p.gambar
            HAVING COALESCE(SUM(td.quantity), 0) > 0
            ORDER BY total_sold DESC, avg_rating DESC
            LIMIT ?
            """;

        try (Connection conn = DatabaseHomepage.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to fetch top selling products: No database connection.");
                return topSellingProducts;
            }

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            int index = 1;
            while (rs.next()) {
                int id = rs.getInt("id_produk");
                String name = rs.getString("nama_produk");
                String description = rs.getString("deskripsi");
                BigDecimal price = rs.getBigDecimal("harga");
                String genre = rs.getString("genre");
                String variant = rs.getString("varian");
                String imagePath = rs.getString("gambar");
                Float avgRating = rs.getFloat("avg_rating");
                int reviewCount = rs.getInt("review_count");
                
                AllProduct product = new AllProduct(id, name, description, price, genre, variant, imagePath, avgRating, reviewCount);
                topSellingProducts.add(product);
                index++;
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching top selling products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return topSellingProducts;
    }

    public static List<AllProduct> getRecommendedProducts(int limit) {
        List<AllProduct> recommendedProducts = new ArrayList<>();
        
        String sql = """
            SELECT 
                p.id_produk,
                p.nama_produk,
                p.deskripsi,
                p.harga,
                p.genre,
                p.varian,
                p.gambar,
                AVG(pr.star) as avg_rating,
                COUNT(pr.id_review) as review_count
            FROM products p
            INNER JOIN product_reviews pr ON p.id_produk = pr.id_produk
            GROUP BY p.id_produk, p.nama_produk, p.deskripsi, p.harga, p.genre, p.varian, p.gambar
            HAVING COUNT(pr.id_review) >= 3 AND AVG(pr.star) >= 4.0
            ORDER BY avg_rating DESC, review_count DESC
            LIMIT ?
            """;

        try (Connection conn = DatabaseHomepage.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to fetch recommended products: No database connection.");
                return recommendedProducts;
            }

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id_produk");
                String name = rs.getString("nama_produk");
                String description = rs.getString("deskripsi");
                BigDecimal price = rs.getBigDecimal("harga");
                String genre = rs.getString("genre");
                String variant = rs.getString("varian");
                String imagePath = rs.getString("gambar");
                Float avgRating = rs.getFloat("avg_rating");
                int reviewCount = rs.getInt("review_count");
                
                AllProduct product = new AllProduct(id, name, description, price, genre, variant, imagePath, avgRating, reviewCount);
                recommendedProducts.add(product);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching recommended products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return recommendedProducts;
    }

    public static int getProductSalesCount(int productId) {
        String sql = """
            SELECT COALESCE(SUM(td.quantity), 0) as total_sold
            FROM transaction_details td
            JOIN transactions t ON td.id_transaksi = t.id_transaksi
            WHERE td.id_produk = ? AND t.payment_status IN ('Completed', 'Success', 'Paid')
            """;

        try (Connection conn = DatabaseHomepage.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to get product sales count: No database connection.");
                return 0;
            }

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_sold");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting product sales count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public static double getProductAverageRating(int productId) {
        String sql = "SELECT COALESCE(AVG(star), 0.0) as avg_rating FROM product_reviews WHERE id_produk = ?";

        try (Connection conn = DatabaseHomepage.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) {
                System.err.println("Failed to get product average rating: No database connection.");
                return 0.0;
            }

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting product average rating: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
}