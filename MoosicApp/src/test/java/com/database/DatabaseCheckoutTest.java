package com.database;

import com.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseCheckoutTest {

    private Order validOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        System.out.println("\n🧪 Setting up test data...");
        testProduct = new Product(
            1, 
            "Midnights Vinyl - Taylor Swift", 
            "Exclusive Midnights edition", 
            new BigDecimal("525000"), 
            "Pop", 
            "Vinyl",
            "midnights.png",
            new ArrayList<ProductReview>()  // FIXED: Added ProductReview generic type
        );
        
        validOrder = createValidOrder();
        System.out.println("✅ Test data setup complete");
    }

    @Test
    @DisplayName("✅ Test: Place Order Success")
    void testPlaceOrderSuccess() {
        System.out.println("\n🧪 Testing: Place Order Success");
        
        boolean result = DatabaseCheckoutMock.placeOrder(validOrder, 1);
        
        assertTrue(result, "Order should be placed successfully");
        System.out.println("✅ PASSED: Order placed successfully");
    }

    @Test
    @DisplayName("❌ Test: Place Order Fails If Order Is Null")
    void testPlaceOrderFailsIfOrderNull() {
        System.out.println("\n🧪 Testing: Place Order with Null Order");
        
        boolean result = DatabaseCheckoutMock.placeOrder(null, 1);
        
        assertFalse(result, "Order should fail when order is null");
        System.out.println("✅ PASSED: Correctly rejected null order");
    }

    @Test
    @DisplayName("❌ Test: Place Order Fails If Order Has No Items")
    void testPlaceOrderFailsIfOrderEmpty() {
        System.out.println("\n🧪 Testing: Place Order with Empty Order");
        
        Order emptyOrder = new Order(); // tidak ada item
        boolean result = DatabaseCheckoutMock.placeOrder(emptyOrder, 1);
        
        assertFalse(result, "Order should fail when no items");
        System.out.println("✅ PASSED: Correctly rejected empty order");
    }

    @Test
    @DisplayName("❌ Test: Place Order Fails If Customer ID Invalid")
    void testPlaceOrderFailsIfInvalidCustomerId() {
        System.out.println("\n🧪 Testing: Place Order with Invalid Customer ID");
        
        boolean result = DatabaseCheckoutMock.placeOrder(validOrder, -1);
        
        assertFalse(result, "Order should fail with invalid customer ID");
        System.out.println("✅ PASSED: Correctly rejected invalid customer ID");
    }

    @Test
    @DisplayName("❌ Test: Place Order Fails If Missing Required Fields")
    void testPlaceOrderFailsIfMissingRequiredFields() {
        System.out.println("\n🧪 Testing: Place Order with Missing Required Fields");
        
        Order incompleteOrder = createValidOrder();
        incompleteOrder.setShippingName(""); // Remove required field
        
        boolean result = DatabaseCheckoutMock.placeOrder(incompleteOrder, 1);
        
        assertFalse(result, "Order should fail when missing required fields");
        System.out.println("✅ PASSED: Correctly rejected order with missing fields");
    }

    // ==================== PROMO CODE TESTS ====================

    @Test
    @DisplayName("✅ Test: Valid Promo Code PROMO123")
    void testPromoCodeValid() {
        System.out.println("\n🧪 Testing: Valid Promo Code PROMO123");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("PROMO123");
        
        assertNotNull(promo, "Promo should not be null");
        assertEquals("PROMO123", promo.getPromo_code(), "Promo code should match");
        assertEquals(new BigDecimal("10.0"), promo.getPersentase(), "Discount should be 10%");
        assertTrue(promo.isBerlaku(), "Promo should be active"); // FIXED: Using isBerlaku()
        
        System.out.println("✅ PASSED: PROMO123 validated successfully");
    }

    @Test
    @DisplayName("✅ Test: Valid Promo Code SAVE20")
    void testPromoCodeSave20Valid() {
        System.out.println("\n🧪 Testing: Valid Promo Code SAVE20");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("SAVE20");
        
        assertNotNull(promo, "Promo should not be null");
        assertEquals("SAVE20", promo.getPromo_code(), "Promo code should match");
        assertEquals(new BigDecimal("20.0"), promo.getPersentase(), "Discount should be 20%");
        assertTrue(promo.isBerlaku(), "Promo should be active"); // FIXED: Using isBerlaku()
        
        System.out.println("✅ PASSED: SAVE20 validated successfully");
    }

    @Test
    @DisplayName("✅ Test: Valid Promo Code NEWUSER")
    void testPromoCodeNewUserValid() {
        System.out.println("\n🧪 Testing: Valid Promo Code NEWUSER");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("NEWUSER");
        
        assertNotNull(promo, "Promo should not be null");
        assertEquals("NEWUSER", promo.getPromo_code(), "Promo code should match");
        assertEquals(new BigDecimal("15.0"), promo.getPersentase(), "Discount should be 15%");
        assertTrue(promo.isBerlaku(), "Promo should be active"); // FIXED: Using isBerlaku()
        
        System.out.println("✅ PASSED: NEWUSER validated successfully");
    }

    @Test
    @DisplayName("❌ Test: Invalid Promo Code")
    void testPromoCodeInvalid() {
        System.out.println("\n🧪 Testing: Invalid Promo Code");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("INVALIDCODE");
        
        assertNull(promo, "Invalid promo should return null");
        System.out.println("✅ PASSED: Correctly rejected invalid promo code");
    }

    @Test
    @DisplayName("❌ Test: Empty Promo Code")
    void testPromoCodeEmpty() {
        System.out.println("\n🧪 Testing: Empty Promo Code");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("");
        
        assertNull(promo, "Empty promo should return null");
        System.out.println("✅ PASSED: Correctly rejected empty promo code");
    }

    @Test
    @DisplayName("❌ Test: Null Promo Code")
    void testPromoCodeNull() {
        System.out.println("\n🧪 Testing: Null Promo Code");
        
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode(null);
        
        assertNull(promo, "Null promo should return null");
        System.out.println("✅ PASSED: Correctly rejected null promo code");
    }

    @Test
    @DisplayName("✅ Test: Valid Credit Card")
    void testValidCreditCard() {
        System.out.println("\n🧪 Testing: Valid Credit Card");
        
        boolean isValid = DatabaseCheckoutMock.isValidCreditCard("1234567890123456");
        
        assertTrue(isValid, "Credit card should be valid");
        System.out.println("✅ PASSED: Credit card validation works");
    }

    @Test
    @DisplayName("❌ Test: Invalid Credit Card")
    void testInvalidCreditCard() {
        System.out.println("\n🧪 Testing: Invalid Credit Card");
        
        boolean isValid = DatabaseCheckoutMock.isValidCreditCard("12345");
        
        assertFalse(isValid, "Short credit card should be invalid");
        System.out.println("✅ PASSED: Invalid credit card correctly rejected");
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("🔄 Integration Test: Complete Order Flow")
    void testCompleteOrderFlow() {
        System.out.println("\n🧪 Testing: Complete Order Flow Integration");
        
        // Step 1: Validate customer
        boolean customerExists = DatabaseCheckoutMock.isCustomerExists(1);
        assertTrue(customerExists, "Customer should exist");
        System.out.println("   ✅ Step 1: Customer validation passed");
        
        // Step 2: Validate promo code
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("PROMO123");
        assertNotNull(promo, "Promo should be valid");
        assertTrue(promo.isBerlaku(), "Promo should be active"); // FIXED: Using isBerlaku()
        System.out.println("   ✅ Step 2: Promo validation passed");
        
        // Step 3: Validate credit card
        boolean cardValid = DatabaseCheckoutMock.isValidCreditCard("1234567890123456");
        assertTrue(cardValid, "Credit card should be valid");
        System.out.println("   ✅ Step 3: Credit card validation passed");
        
        // Step 4: Place order
        boolean orderPlaced = DatabaseCheckoutMock.placeOrder(validOrder, 1);
        assertTrue(orderPlaced, "Order should be placed successfully");
        System.out.println("   ✅ Step 4: Order placement passed");
        
        System.out.println("✅ PASSED: Complete order flow works end-to-end");
    }

    @Test
    @DisplayName("🔍 Edge Case: Case Insensitive Promo Codes")
    void testCaseInsensitivePromoCodes() {
        System.out.println("\n🧪 Testing: Case Insensitive Promo Codes");
        
        // Test lowercase
        DiscountPromo promo1 = DatabaseCheckoutMock.validatePromoCode("promo123");
        assertNotNull(promo1, "Lowercase promo should work");
        assertEquals("PROMO123", promo1.getPromo_code(), "Should return uppercase code");
        
        // Test mixed case
        DiscountPromo promo2 = DatabaseCheckoutMock.validatePromoCode("ProMo123");
        assertNotNull(promo2, "Mixed case promo should work");
        
        System.out.println("✅ PASSED: Case insensitive promo codes work correctly");
    }

    @Test
    @DisplayName("🔍 Edge Case: Credit Card with Spaces and Dashes")
    void testCreditCardFormatting() {
        System.out.println("\n🧪 Testing: Credit Card with Spaces and Dashes");
        
        // Test with spaces
        boolean valid1 = DatabaseCheckoutMock.isValidCreditCard("1234 5678 9012 3456");
        assertTrue(valid1, "Credit card with spaces should be valid");
        
        // Test with dashes
        boolean valid2 = DatabaseCheckoutMock.isValidCreditCard("1234-5678-9012-3456");
        assertTrue(valid2, "Credit card with dashes should be valid");
        
        System.out.println("✅ PASSED: Credit card formatting works correctly");
    }

    /**
     * Creates a valid order for testing
     */
    private Order createValidOrder() {
        OrderItem item = new OrderItem(testProduct, 2);
        
        Order order = new Order();
        order.addItem(item);
        
        // Set required shipping information
        order.setShippingName("John Doe");
        order.setShippingAddress("123 Test Street");
        order.setShippingCity("Test City");
        order.setShippingCountry("Indonesia");
        order.setShippingState("West Java");
        order.setShippingZipCode("12345");
        order.setShippingMobilePhone("08123456789");
        
        // Set required payment information
        order.setCardFullName("John Doe");
        order.setCardNumber("1234567890123456");
        order.setCardExpirationDate("12/25");
        order.setCardCvv("123");
        
        return order;
    }
}