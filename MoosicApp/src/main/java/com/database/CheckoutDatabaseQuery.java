// Lokasi: src/main/java/com/database/DatabaseQuery.java
package com.database;

import com.model.DiscountPromo;
import com.model.Order;
import com.model.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

public class CheckoutDatabaseQuery {
     
    public static boolean placeOrder(Order order, int customerId) {
        String sqlTransactions = "INSERT INTO transactions (id_user, total_harga, payment_method_type, cardholder_name, card_number, card_valid_through, card_cvv, payment_status, applied_promo_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlShipping = "INSERT INTO shipping_info (id_transaksi, recipient_name, address_line, city, state, country, zip_code, mobile_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetails = "INSERT INTO transaction_details (id_transaksi, id_produk, quantity, price_at_transaction) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = CheckoutDatabaseConnection.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false); 

            long transactionId;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTransactions, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, customerId);
                pstmt.setBigDecimal(2, BigDecimal.valueOf(order.calculateTotal()));
                pstmt.setString(3, "Credit Card");
                pstmt.setString(4, order.getCardFullName());
                pstmt.setString(5, order.getCardNumber());
                pstmt.setString(6, order.getCardExpirationDate());
                pstmt.setString(7, order.getCardCvv());
                pstmt.setString(8, "Completed");
                pstmt.setString(9, order.getPromoCode());
                
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transactionId = generatedKeys.getLong("id_transaksi");
                } else {
                    throw new SQLException("Gagal membuat transaksi, tidak mendapatkan ID.");
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlShipping)) {
                pstmt.setLong(1, transactionId);
                pstmt.setString(2, order.getShippingName());
                pstmt.setString(3, order.getShippingAddress());
                pstmt.setString(4, order.getShippingCity());
                pstmt.setString(5, order.getShippingState());
                pstmt.setString(6, order.getShippingCountry());
                pstmt.setString(7, order.getShippingZipCode());
                pstmt.setString(8, order.getShippingMobilePhone());
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlDetails)) {
                for (OrderItem item : order.getItems()) {
                    pstmt.setLong(1, transactionId);
                    pstmt.setInt(2, item.getProduct().getId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setBigDecimal(4, item.getProduct().getPrice());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            conn.commit();
            System.out.println("Order berhasil disimpan dengan ID Transaksi: " + transactionId);
            return true;

        } catch (SQLException e) {
            System.err.println("Transaksi Gagal! Melakukan rollback. Error: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Gagal melakukan rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DiscountPromo validatePromoCode(String promoCode) {
        String sql = "SELECT * FROM discount_promos WHERE promo_code = ? AND berlaku = TRUE AND NOW() BETWEEN valid_from AND valid_until";
        
        try (Connection conn = CheckoutDatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return null;

            pstmt.setString(1, promoCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new DiscountPromo(
                    rs.getInt("id_promo"),
                    rs.getString("nama_promo"),
                    rs.getString("promo_code"),
                    rs.getBigDecimal("persentase"),
                    rs.getBoolean("berlaku"),
                    rs.getObject("valid_from", OffsetDateTime.class),
                    rs.getObject("valid_until", OffsetDateTime.class)
                );
            }
        } catch (SQLException e) {
            System.err.println("Error saat memvalidasi promo code: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}