package com.database;

import com.model.AllProduct;

import java.math.BigDecimal;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShopDAO {

    public static List<AllProduct> getAllProducts() {
        String sql = "SELECT * FROM products ORDER BY nama_produk ASC";
        List<AllProduct> products = new ArrayList<>();

        try (Connection conn = DatabaseConnectShop.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (conn == null) {
                System.err.println("Gagal mendapat koneksi dari Database.");
                return Collections.emptyList();
            }

            while (rs.next()) {
                products.add(new AllProduct(
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getString("deskripsi"),
                    rs.getBigDecimal("harga"),
                    rs.getString("genre"),
                    rs.getString("varian"),
                    rs.getString("gambar"),
                    rs.getFloat("rating"),
                    rs.getInt("terjual")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data produk dari database: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public static AllProduct getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE id_produk = ?";
        AllProduct product = null;

        try (Connection conn = DatabaseConnectShop.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) return null;

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                product = new AllProduct(
                    rs.getInt("id_produk"),
                    rs.getString("nama_produk"),
                    rs.getString("deskripsi"),
                    rs.getBigDecimal("harga"),
                    rs.getString("genre"),
                    rs.getString("varian"),
                    rs.getString("gambar"),
                    rs.getFloat("rating"),
                    rs.getInt("terjual")
                );
            }
        } catch (SQLException e) {
            System.err.println("SQL Error fetching product by ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return product;
    }
}