package com.database;

import com.model.DiscountPromo;
import com.model.Order;
import com.model.OrderItem;
import com.model.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;

/**
 * Mock database class untuk testing fitur checkout
 * Mengemulasi behavior dari CheckoutDatabaseQuery tanpa koneksi database
 */
public class DatabaseCheckoutMock {

    /**
     * Mock method untuk place order
     * @param order Order object yang akan disimpan
     * @param customerId ID customer yang melakukan order
     * @return true jika order berhasil disimpan, false jika gagal
     */
    public static boolean placeOrder(Order order, int customerId) {
        // Validasi input
        if (order == null) {
            System.out.println("❌ Mock: Order is null");
            return false;
        }
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            System.out.println("❌ Mock: Order has no items");
            return false;
        }
        
        if (customerId <= 0) {
            System.out.println("❌ Mock: Invalid customer ID");
            return false;
        }
        
        // Validasi required fields
        if (order.getShippingName() == null || order.getShippingName().trim().isEmpty()) {
            System.out.println("❌ Mock: Shipping name is required");
            return false;
        }
        
        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            System.out.println("❌ Mock: Shipping address is required");
            return false;
        }
        
        // Simulate successful order placement
        System.out.println("✅ Mock: Order placed successfully");
        System.out.println("   Customer ID: " + customerId);
        System.out.println("   Items count: " + order.getItems().size());
        System.out.println("   Total: " + order.calculateSubtotal());
        
        return true;
    }

    /**
     * Mock method untuk validasi promo code
     * @param promoCode Kode promo yang akan divalidasi
     * @return DiscountPromo object jika valid, null jika tidak valid
     */
    public static DiscountPromo validatePromoCode(String promoCode) {
        if (promoCode == null || promoCode.trim().isEmpty()) {
            System.out.println("❌ Mock: Promo code is empty");
            return null;
        }
        
        // Mock promo codes yang valid
        switch (promoCode.toUpperCase()) {
            case "PROMO123":
                System.out.println("✅ Mock: Valid promo code PROMO123 (10% discount)");
                return new DiscountPromo(
                    1, 
                    "Promo Diskon 10%", 
                    "PROMO123",
                    new BigDecimal("10.0"), 
                    true,
                    OffsetDateTime.now().minusDays(30), 
                    OffsetDateTime.now().plusDays(30)
                );
                
            case "SAVE20":
                System.out.println("✅ Mock: Valid promo code SAVE20 (20% discount)");
                return new DiscountPromo(
                    2, 
                    "Promo Diskon 20%", 
                    "SAVE20",
                    new BigDecimal("20.0"), 
                    true,
                    OffsetDateTime.now().minusDays(15), 
                    OffsetDateTime.now().plusDays(15)
                );
                
            case "NEWUSER":
                System.out.println("✅ Mock: Valid promo code NEWUSER (15% discount)");
                return new DiscountPromo(
                    3, 
                    "Promo New User 15%", 
                    "NEWUSER",
                    new BigDecimal("15.0"), 
                    true,
                    OffsetDateTime.now().minusDays(7), 
                    OffsetDateTime.now().plusDays(7)
                );
                
            default:
                System.out.println("❌ Mock: Invalid promo code: " + promoCode);
                return null;
        }
    }
    
    /**
     * Mock method untuk mendapatkan customer berdasarkan ID
     * @param customerId ID customer
     * @return true jika customer exists, false jika tidak
     */
    public static boolean isCustomerExists(int customerId) {
        // Mock: customer ID 1-100 dianggap valid
        boolean exists = customerId > 0 && customerId <= 100;
        if (exists) {
            System.out.println("✅ Mock: Customer " + customerId + " exists");
        } else {
            System.out.println("❌ Mock: Customer " + customerId + " not found");
        }
        return exists;
    }
    
    /**
     * Mock method untuk validasi credit card
     * @param cardNumber Nomor credit card
     * @return true jika valid, false jika tidak valid
     */
    public static boolean isValidCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            System.out.println("❌ Mock: Credit card number is empty");
            return false;
        }
        
        // Remove spaces and dashes
        String cleanCardNumber = cardNumber.replaceAll("[\\s-]", "");
        
        // Basic validation: should be 16 digits
        if (!cleanCardNumber.matches("\\d{16}")) {
            System.out.println("❌ Mock: Invalid credit card format");
            return false;
        }
        
        System.out.println("✅ Mock: Valid credit card number");
        return true;
    }
}