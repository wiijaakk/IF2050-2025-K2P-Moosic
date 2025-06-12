package com.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import com.model.Product;
import java.math.BigDecimal;

/**
 * FINAL: Unit Test untuk HomepageController
 * Handle double price type correctly
 */
public class HomepageControllerTest {

    private HomepageController homepageController;

    @BeforeEach
    void setUp() {
        homepageController = new HomepageController();
    }

    // ===============================================
    // TEST GROUP 1: Search Validation Logic
    // ===============================================
    
    @Nested
    @DisplayName("Search Validation Tests")
    class SearchValidationTests {
        
        @Test
        @DisplayName("Should validate valid search query")
        void testValidSearchQuery() {
            String validQuery = "Taylor Swift";
            boolean result = homepageController.isValidSearchQuery(validQuery);
            assertTrue(result, "Valid search query should return true");
        }
        
        @Test
        @DisplayName("Should reject null search query")
        void testNullSearchQuery() {
            String nullQuery = null;
            boolean result = homepageController.isValidSearchQuery(nullQuery);
            assertFalse(result, "Null search query should return false");
        }
        
        @Test
        @DisplayName("Should reject empty search query")
        void testEmptySearchQuery() {
            String emptyQuery = "";
            boolean result = homepageController.isValidSearchQuery(emptyQuery);
            assertFalse(result, "Empty search query should return false");
        }
        
        @Test
        @DisplayName("Should reject whitespace-only search query")
        void testWhitespaceSearchQuery() {
            String whitespaceQuery = "   ";
            boolean result = homepageController.isValidSearchQuery(whitespaceQuery);
            assertFalse(result, "Whitespace-only search query should return false");
        }
        
        @Test
        @DisplayName("Should reject very long search query")
        void testVeryLongSearchQuery() {
            String longQuery = "a".repeat(200);
            boolean result = homepageController.isValidSearchQuery(longQuery);
            assertFalse(result, "Very long search query should return false");
        }
        
        @Test
        @DisplayName("Should format search query correctly")
        void testFormatSearchQuery() {
            String inputQuery = "  TAYLOR SWIFT  ";
            String expectedOutput = "taylor swift";
            String result = homepageController.formatSearchQuery(inputQuery);
            assertEquals(expectedOutput, result, "Search query should be trimmed and lowercased");
        }
    }

    // ===============================================
    // TEST GROUP 2: Product Validation Logic
    // ===============================================
    
    @Nested
    @DisplayName("Product Validation Tests")
    class ProductValidationTests {
        
        @Test
        @DisplayName("Should validate valid product")
        void testValidProduct() {
            Product validProduct = new Product(
                1, "Midnights Vinyl", "Taylor Swift album", 
                new BigDecimal("525000"), "Pop", "Vinyl", "midnights.png", "4.5 ⭐"
            );
            
            boolean result = homepageController.isValidProduct(validProduct);
            assertTrue(result, "Valid product should return true");
        }
        
        @Test
        @DisplayName("Should reject null product")
        void testNullProduct() {
            Product nullProduct = null;
            boolean result = homepageController.isValidProduct(nullProduct);
            assertFalse(result, "Null product should return false");
        }
        
        @Test
        @DisplayName("Should reject product with null name")
        void testProductWithNullName() {
            Product productWithNullName = new Product(
                1, null, "Description", new BigDecimal("100000"), 
                "Pop", "CD", "image.png", "4.0 ⭐"
            );
            
            boolean result = homepageController.isValidProduct(productWithNullName);
            assertFalse(result, "Product with null name should return false");
        }
        
        @Test
        @DisplayName("Should reject product with empty name")
        void testProductWithEmptyName() {
            Product productWithEmptyName = new Product(
                1, "", "Description", new BigDecimal("100000"), 
                "Pop", "CD", "image.png", "4.0 ⭐"
            );
            
            boolean result = homepageController.isValidProduct(productWithEmptyName);
            assertFalse(result, "Product with empty name should return false");
        }
    }

    // ===============================================
    // TEST GROUP 3: Shopping Cart Logic
    // ===============================================
    
    @Nested
    @DisplayName("Shopping Cart Tests")
    class ShoppingCartTests {
        
        @Test
        @DisplayName("Should calculate empty cart total as zero")
        void testEmptyCartTotal() {
            double total = homepageController.calculateCartTotalAsDouble();
            assertEquals(0.0, total, 0.01, "Empty cart total should be zero");
        }
        
        @Test
        @DisplayName("Should calculate cart total correctly")
        void testCartTotalCalculation() {
            Product product1 = new Product(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", "4.0 ⭐");
            Product product2 = new Product(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", "4.5 ⭐");
            
            homepageController.getShoppingCart().add(product1);
            homepageController.getShoppingCart().add(product2);
            
            double total = homepageController.calculateCartTotalAsDouble();
            assertEquals(250000.0, total, 0.01, "Cart total should be sum of all product prices");
        }
        
        @Test
        @DisplayName("Should return correct cart item count")
        void testCartItemCount() {
            Product product1 = new Product(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", "4.0 ⭐");
            Product product2 = new Product(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", "4.5 ⭐");
            
            assertEquals(0, homepageController.getCartItemCount(), "Empty cart should have 0 items");
            
            homepageController.getShoppingCart().add(product1);
            assertEquals(1, homepageController.getCartItemCount(), "Cart should have 1 item");
            
            homepageController.getShoppingCart().add(product2);
            assertEquals(2, homepageController.getCartItemCount(), "Cart should have 2 items");
        }
        
        @Test
        @DisplayName("Should clear cart successfully")
        void testClearCart() {
            Product product = new Product(1, "Album", "Desc", new BigDecimal("100000"), "Pop", "CD", "img.png", "4.0 ⭐");
            homepageController.getShoppingCart().add(product);
            
            homepageController.clearCart();
            
            assertEquals(0, homepageController.getCartItemCount(), "Cart should be empty after clearing");
            assertEquals(0.0, homepageController.calculateCartTotalAsDouble(), 0.01, "Cart total should be zero after clearing");
        }
        
        @Test
        @DisplayName("Should check if product exists in cart")
        void testProductInCart() {
            Product product1 = new Product(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", "4.0 ⭐");
            Product product2 = new Product(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", "4.5 ⭐");
            
            homepageController.getShoppingCart().add(product1);
            
            assertTrue(homepageController.isProductInCart(product1), "Product1 should be in cart");
            assertFalse(homepageController.isProductInCart(product2), "Product2 should not be in cart");
        }
        
        @Test
        @DisplayName("Should count products by genre in cart")
        void testCartProductCountByGenre() {
            Product popAlbum1 = new Product(1, "Pop Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", "4.0 ⭐");
            Product popAlbum2 = new Product(2, "Pop Album 2", "Desc", new BigDecimal("120000"), "Pop", "Vinyl", "img2.png", "4.2 ⭐");
            Product rockAlbum = new Product(3, "Rock Album", "Desc", new BigDecimal("150000"), "Rock", "CD", "img3.png", "4.5 ⭐");
            
            homepageController.getShoppingCart().add(popAlbum1);
            homepageController.getShoppingCart().add(popAlbum2);
            homepageController.getShoppingCart().add(rockAlbum);
            
            assertEquals(2, homepageController.getCartProductCountByGenre("Pop"), "Should have 2 Pop albums in cart");
            assertEquals(1, homepageController.getCartProductCountByGenre("Rock"), "Should have 1 Rock album in cart");
            assertEquals(0, homepageController.getCartProductCountByGenre("Jazz"), "Should have 0 Jazz albums in cart");
        }
    }

    // ===============================================
    // TEST GROUP 4: Basic Integration Tests
    // ===============================================
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete workflow")
        void testCompleteWorkflow() {
            // Test search
            assertTrue(homepageController.isValidSearchQuery("test query"));
            assertEquals("formatted", homepageController.formatSearchQuery("  FORMATTED  "));
            
            // Test product validation
            Product validProduct = new Product(1, "Test Album", "Desc", new BigDecimal("50000"), "Pop", "CD", "test.png", "4.0 ⭐");
            assertTrue(homepageController.isValidProduct(validProduct));
            
            // Test cart operations
            homepageController.getShoppingCart().add(validProduct);
            assertEquals(1, homepageController.getCartItemCount());
            assertTrue(homepageController.isProductInCart(validProduct));
            assertEquals(50000.0, homepageController.calculateCartTotalAsDouble(), 0.01);
            
            // Test clear cart
            homepageController.clearCart();
            assertEquals(0, homepageController.getCartItemCount());
        }
        
        @Test
        @DisplayName("Should handle edge cases gracefully")
        void testEdgeCases() {
            // Null inputs
            assertFalse(homepageController.isValidSearchQuery(null));
            assertFalse(homepageController.isValidProduct(null));
            assertEquals("", homepageController.formatSearchQuery(null));
            
            // Empty inputs
            assertFalse(homepageController.isValidSearchQuery(""));
            assertEquals(0, homepageController.getCartItemCount());
            assertEquals(0.0, homepageController.calculateCartTotalAsDouble(), 0.01);
        }
    }
}