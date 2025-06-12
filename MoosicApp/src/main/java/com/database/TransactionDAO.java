package com.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class TransactionDAO {
    public static void createTransaction(int userId, Map<Integer, Integer> cart) {
        String insertTransactionSQL = "INSERT INTO transactions (id_user, total_harga, payment_status) VALUES (?, ?, 'Completed') RETURNING id_transaksi";
        String insertDetailSQL = "INSERT INTO transaction_details (id_transaksi, id_produk, quantity, price_at_transaction) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("createTransaction failed because database connection is null.");
                return;
            }
            conn.setAutoCommit(false); 

            BigDecimal totalPrice = BigDecimal.ZERO;
            for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                totalPrice = totalPrice.add(getProductPrice(conn, item.getKey()).multiply(new BigDecimal(item.getValue())));
            }

            int transactionId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertTransactionSQL)) {
                pstmt.setInt(1, userId);
                pstmt.setBigDecimal(2, totalPrice);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    transactionId = rs.getInt("id_transaksi");
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertDetailSQL)) {
                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {
                    pstmt.setInt(1, transactionId);
                    pstmt.setInt(2, item.getKey());
                    pstmt.setInt(3, item.getValue());
                    pstmt.setBigDecimal(4, getProductPrice(conn, item.getKey()));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit(); 
            System.out.println("Transaction created successfully with ID: " + transactionId);
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    private static BigDecimal getProductPrice(Connection conn, int productId) throws SQLException {
        String sql = "SELECT harga FROM products WHERE id_produk = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("harga");
            }
        }
        throw new SQLException("Product with ID " + productId + " not found.");
    }
}
