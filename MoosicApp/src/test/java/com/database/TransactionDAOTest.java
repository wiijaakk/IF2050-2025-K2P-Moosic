package com.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDAOTest {

    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DatabaseConnection.getConnection();
        assertNotNull(conn, "Koneksi ke database gagal, periksa konfigurasi.");
        conn.setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.rollback(); // Membatalkan semua perubahan setelah tes
            conn.close();
        }
    }

    @Test
    @DisplayName("Test createTransaction dengan keranjang yang valid")
    void testCreateTransaction_ValidCart_ShouldInsertTransactionAndDetails() throws SQLException {
        int userId = 1;
        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 2);
        cart.put(2, 1);

        // Menghitung total harga yang diharapkan
        BigDecimal priceOfProduct1 = getProductPrice(1);
        BigDecimal priceOfProduct2 = getProductPrice(2);
        BigDecimal expectedTotalPrice = priceOfProduct1.multiply(new BigDecimal(2)).add(priceOfProduct2);

        TransactionDAO.createTransaction(userId, cart);

        PreparedStatement pstmtTx = conn.prepareStatement("SELECT * FROM transactions ORDER BY created_at DESC LIMIT 1");
        ResultSet rsTx = pstmtTx.executeQuery();
        
        assertTrue(rsTx.next(), "Transaksi baru seharusnya dibuat di tabel transactions.");
        int newTransactionId = rsTx.getInt("id_transaksi");
        assertEquals(userId, rsTx.getInt("id_user"));
        assertEquals(0, expectedTotalPrice.compareTo(rsTx.getBigDecimal("total_harga")), "Total harga tidak sesuai.");
        assertEquals("Completed", rsTx.getString("payment_status"));

        PreparedStatement pstmtDetail = conn.prepareStatement("SELECT * FROM transaction_details WHERE id_transaksi = ?");
        pstmtDetail.setInt(1, newTransactionId);
        ResultSet rsDetail = pstmtDetail.executeQuery();

        int detailCount = 0;
        while(rsDetail.next()) {
            detailCount++;
            int productId = rsDetail.getInt("id_produk");
            assertTrue(cart.containsKey(productId), "Produk di detail transaksi seharusnya ada di keranjang.");
            assertEquals(cart.get(productId).intValue(), rsDetail.getInt("quantity"));
        }
        assertEquals(cart.size(), detailCount, "Jumlah item di detail transaksi harus sama dengan jumlah item di keranjang.");
    }

    @Test
    @DisplayName("Test createTransaction dengan keranjang kosong")
    void testCreateTransaction_EmptyCart_ShouldCreateTransactionWithZeroTotal() throws SQLException {
        int userId = 1;
        Map<Integer, Integer> cart = new HashMap<>();

        TransactionDAO.createTransaction(userId, cart);
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM transactions ORDER BY created_at DESC LIMIT 1");
        ResultSet rs = pstmt.executeQuery();

        assertTrue(rs.next(), "Transaksi seharusnya tetap dibuat meskipun keranjang kosong.");
        assertEquals(userId, rs.getInt("id_user"));
        assertEquals(0, BigDecimal.ZERO.compareTo(rs.getBigDecimal("total_harga")), "Total harga seharusnya 0 untuk keranjang kosong.");
    }
    
    @Test
    @DisplayName("Test createTransaction dengan produk tidak valid di keranjang")
    void testCreateTransaction_InvalidProductInCart_ShouldRollbackAndNotCreateTransaction() throws SQLException {
        int userId = 1;
        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(9999, 1);

        long initialTransactionCount = getTransactionCount();
        TransactionDAO.createTransaction(userId, cart);

        long finalTransactionCount = getTransactionCount();
        assertEquals(initialTransactionCount, finalTransactionCount, "Seharusnya tidak ada transaksi baru yang dibuat jika ada produk tidak valid.");
    }

    private BigDecimal getProductPrice(int productId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT harga FROM products WHERE id_produk = ?")) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("harga");
            }
            throw new SQLException("Produk tes dengan ID " + productId + " tidak ditemukan.");
        }
    }

    private long getTransactionCount() throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT count(*) AS total FROM transactions")) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("total");
            }
            return 0;
        }
    }
}