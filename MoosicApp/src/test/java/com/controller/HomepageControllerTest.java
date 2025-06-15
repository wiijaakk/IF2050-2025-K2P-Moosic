package com.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import com.model.AllProduct;
import java.math.BigDecimal;

/**
 * FIXED: Unit Test untuk HomepageController
 * Updated to use correct AllProduct constructor (9 parameters)
 * Fixed constructor calls to match: (int id, String name, String desc, BigDecimal price, String genre, String variant, String image, Float rating, int sold)
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
    // TEST GROUP 2: AllProduct Validation Logic
    // ===============================================
    
    @Nested
    @DisplayName("AllProduct Validation Tests")
    class AllProductValidationTests {
        
        @Test
        @DisplayName("Should validate valid AllProduct")
        void testValidAllProduct() {
            // FIXED: Use correct constructor (id, name, desc, price, genre, variant, image, rating, sold)
            AllProduct validProduct = new AllProduct(
                1, "Midnights Vinyl", "Taylor Swift album", 
                new BigDecimal("525000"), "Pop", "Vinyl", "midnights.png", 4.5f, 15
            );
            
            boolean result = homepageController.isValidProduct(validProduct);
            assertTrue(result, "Valid AllProduct should return true");
        }
        
        @Test
        @DisplayName("Should reject null AllProduct")
        void testNullAllProduct() {
            AllProduct nullProduct = null;
            boolean result = homepageController.isValidProduct(nullProduct);
            assertFalse(result, "Null AllProduct should return false");
        }
        
        @Test
        @DisplayName("Should reject AllProduct with null name")
        void testAllProductWithNullName() {
            // FIXED: Use correct constructor
            AllProduct productWithNullName = new AllProduct(
                1, null, "Description", new BigDecimal("100000"), 
                "Pop", "CD", "image.png", 4.0f, 10
            );
            
            boolean result = homepageController.isValidProduct(productWithNullName);
            assertFalse(result, "AllProduct with null name should return false");
        }
        
        @Test
        @DisplayName("Should reject AllProduct with empty name")
        void testAllProductWithEmptyName() {
            // FIXED: Use correct constructor
            AllProduct productWithEmptyName = new AllProduct(
                1, "", "Description", new BigDecimal("100000"), 
                "Pop", "CD", "image.png", 4.0f, 10
            );
            
            boolean result = homepageController.isValidProduct(productWithEmptyName);
            assertFalse(result, "AllProduct with empty name should return false");
        }
        
        @Test
        @DisplayName("Should validate AllProduct with different genres")
        void testAllProductDifferentGenres() {
            // FIXED: Use correct constructor for all products
            AllProduct popProduct = new AllProduct(
                1, "Pop Album", "Description", new BigDecimal("100000"), 
                "Pop", "CD", "pop.png", 4.0f, 5
            );
            
            AllProduct rockProduct = new AllProduct(
                2, "Rock Album", "Description", new BigDecimal("150000"), 
                "Rock", "Vinyl", "rock.png", 4.5f, 8
            );
            
            AllProduct jazzProduct = new AllProduct(
                3, "Jazz Album", "Description", new BigDecimal("200000"), 
                "Jazz", "Cassette", "jazz.png", 4.8f, 12
            );
            
            assertTrue(homepageController.isValidProduct(popProduct), "Pop AllProduct should be valid");
            assertTrue(homepageController.isValidProduct(rockProduct), "Rock AllProduct should be valid");
            assertTrue(homepageController.isValidProduct(jazzProduct), "Jazz AllProduct should be valid");
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
            // FIXED: Use correct constructor
            AllProduct product1 = new AllProduct(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", 4.0f, 5);
            AllProduct product2 = new AllProduct(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", 4.5f, 8);
            
            homepageController.getShoppingCart().add(product1);
            homepageController.getShoppingCart().add(product2);
            
            double total = homepageController.calculateCartTotalAsDouble();
            assertEquals(250000.0, total, 0.01, "Cart total should be sum of all AllProduct prices");
        }
        
        @Test
        @DisplayName("Should return correct cart item count")
        void testCartItemCount() {
            // FIXED: Use correct constructor
            AllProduct product1 = new AllProduct(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", 4.0f, 5);
            AllProduct product2 = new AllProduct(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", 4.5f, 8);
            
            assertEquals(0, homepageController.getCartItemCount(), "Empty cart should have 0 items");
            
            homepageController.getShoppingCart().add(product1);
            assertEquals(1, homepageController.getCartItemCount(), "Cart should have 1 item");
            
            homepageController.getShoppingCart().add(product2);
            assertEquals(2, homepageController.getCartItemCount(), "Cart should have 2 items");
        }
        
        @Test
        @DisplayName("Should clear cart successfully")
        void testClearCart() {
            // FIXED: Use correct constructor
            AllProduct product = new AllProduct(1, "Album", "Desc", new BigDecimal("100000"), "Pop", "CD", "img.png", 4.0f, 5);
            homepageController.getShoppingCart().add(product);
            
            homepageController.clearCart();
            
            assertEquals(0, homepageController.getCartItemCount(), "Cart should be empty after clearing");
            assertEquals(0.0, homepageController.calculateCartTotalAsDouble(), 0.01, "Cart total should be zero after clearing");
        }
        
        @Test
        @DisplayName("Should check if AllProduct exists in cart")
        void testAllProductInCart() {
            // FIXED: Use correct constructor
            AllProduct product1 = new AllProduct(1, "Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", 4.0f, 5);
            AllProduct product2 = new AllProduct(2, "Album 2", "Desc", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", 4.5f, 8);
            
            homepageController.getShoppingCart().add(product1);
            
            assertTrue(homepageController.isProductInCart(product1), "AllProduct1 should be in cart");
            assertFalse(homepageController.isProductInCart(product2), "AllProduct2 should not be in cart");
        }
        
        @Test
        @DisplayName("Should count AllProducts by genre in cart")
        void testCartAllProductCountByGenre() {
            // FIXED: Use correct constructor
            AllProduct popAlbum1 = new AllProduct(1, "Pop Album 1", "Desc", new BigDecimal("100000"), "Pop", "CD", "img1.png", 4.0f, 5);
            AllProduct popAlbum2 = new AllProduct(2, "Pop Album 2", "Desc", new BigDecimal("120000"), "Pop", "Vinyl", "img2.png", 4.2f, 7);
            AllProduct rockAlbum = new AllProduct(3, "Rock Album", "Desc", new BigDecimal("150000"), "Rock", "CD", "img3.png", 4.5f, 8);
            
            homepageController.getShoppingCart().add(popAlbum1);
            homepageController.getShoppingCart().add(popAlbum2);
            homepageController.getShoppingCart().add(rockAlbum);
            
            assertEquals(2, homepageController.getCartProductCountByGenre("Pop"), "Should have 2 Pop albums in cart");
            assertEquals(1, homepageController.getCartProductCountByGenre("Rock"), "Should have 1 Rock album in cart");
            assertEquals(0, homepageController.getCartProductCountByGenre("Jazz"), "Should have 0 Jazz albums in cart");
        }
        
        @Test
        @DisplayName("Should handle mixed format AllProducts in cart")
        void testCartMixedFormatAllProducts() {
            // FIXED: Use correct constructor
            AllProduct cdAlbum = new AllProduct(1, "CD Album", "Desc", new BigDecimal("75000"), "Pop", "CD", "cd.png", 4.0f, 3);
            AllProduct vinylAlbum = new AllProduct(2, "Vinyl Album", "Desc", new BigDecimal("125000"), "Rock", "Vinyl", "vinyl.png", 4.5f, 6);
            AllProduct cassetteAlbum = new AllProduct(3, "Cassette Album", "Desc", new BigDecimal("50000"), "Jazz", "Cassette", "cassette.png", 4.2f, 2);
            
            homepageController.getShoppingCart().add(cdAlbum);
            homepageController.getShoppingCart().add(vinylAlbum);
            homepageController.getShoppingCart().add(cassetteAlbum);
            
            assertEquals(3, homepageController.getCartItemCount(), "Should have 3 AllProducts of different formats");
            assertEquals(250000.0, homepageController.calculateCartTotalAsDouble(), 0.01, "Should calculate total correctly for mixed formats");
        }
    }

    // ===============================================
    // TEST GROUP 4: Basic Integration Tests
    // ===============================================
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete workflow with AllProduct")
        void testCompleteWorkflowWithAllProduct() {
            // Test search
            assertTrue(homepageController.isValidSearchQuery("test query"));
            assertEquals("formatted", homepageController.formatSearchQuery("  FORMATTED  "));
            
            // Test AllProduct validation - FIXED: Use correct constructor
            AllProduct validProduct = new AllProduct(1, "Test Album", "Desc", new BigDecimal("50000"), "Pop", "CD", "test.png", 4.0f, 3);
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
        @DisplayName("Should handle edge cases gracefully with AllProduct")
        void testEdgeCasesWithAllProduct() {
            // Null inputs
            assertFalse(homepageController.isValidSearchQuery(null));
            assertFalse(homepageController.isValidProduct(null));
            assertEquals("", homepageController.formatSearchQuery(null));
            
            // Empty inputs
            assertFalse(homepageController.isValidSearchQuery(""));
            assertEquals(0, homepageController.getCartItemCount());
            assertEquals(0.0, homepageController.calculateCartTotalAsDouble(), 0.01);
        }
        
        @Test
        @DisplayName("Should handle multiple AllProduct operations")
        void testMultipleAllProductOperations() {
            // FIXED: Use correct constructor
            AllProduct product1 = new AllProduct(1, "Album 1", "Desc 1", new BigDecimal("100000"), "Pop", "CD", "img1.png", 4.0f, 5);
            AllProduct product2 = new AllProduct(2, "Album 2", "Desc 2", new BigDecimal("150000"), "Rock", "Vinyl", "img2.png", 4.5f, 8);
            AllProduct product3 = new AllProduct(3, "Album 3", "Desc 3", new BigDecimal("200000"), "Jazz", "Cassette", "img3.png", 4.8f, 12);
            
            // Validate all products
            assertTrue(homepageController.isValidProduct(product1));
            assertTrue(homepageController.isValidProduct(product2));
            assertTrue(homepageController.isValidProduct(product3));
            
            // Add to cart
            homepageController.getShoppingCart().add(product1);
            homepageController.getShoppingCart().add(product2);
            homepageController.getShoppingCart().add(product3);
            
            // Verify cart state
            assertEquals(3, homepageController.getCartItemCount());
            assertEquals(450000.0, homepageController.calculateCartTotalAsDouble(), 0.01);
            
            // Test genre counting
            assertEquals(1, homepageController.getCartProductCountByGenre("Pop"));
            assertEquals(1, homepageController.getCartProductCountByGenre("Rock"));
            assertEquals(1, homepageController.getCartProductCountByGenre("Jazz"));
            assertEquals(0, homepageController.getCartProductCountByGenre("Classical"));
        }
    }

    // ===============================================
    // TEST GROUP 5: Additional Edge Cases
    // ===============================================
    
    @Nested
    @DisplayName("Additional Edge Case Tests")
    class AdditionalEdgeCaseTests {
        
        @Test
        @DisplayName("Should handle negative price validation")
        void testNegativePriceValidation() {
            // Test with negative price - this should be handled by isValidProduct method
            AllProduct negativeProduct = new AllProduct(1, "Test Album", "Desc", new BigDecimal("-50000"), "Pop", "CD", "test.png", 4.0f, 3);
            boolean result = homepageController.isValidProduct(negativeProduct);
            assertFalse(result, "Product with negative price should be invalid");
        }
        
        @Test
        @DisplayName("Should handle zero price validation")
        void testZeroPriceValidation() {
            AllProduct zeroProduct = new AllProduct(1, "Free Album", "Desc", new BigDecimal("0"), "Pop", "CD", "test.png", 4.0f, 3);
            boolean result = homepageController.isValidProduct(zeroProduct);
            assertTrue(result, "Product with zero price should be valid");
        }
        
        @Test
        @DisplayName("Should handle very large cart calculations")
        void testLargeCartCalculations() {
            // Add many products to test performance and accuracy
            for (int i = 1; i <= 100; i++) {
                AllProduct product = new AllProduct(i, "Album " + i, "Desc", new BigDecimal("10000"), "Pop", "CD", "test.png", 4.0f, 1);
                homepageController.getShoppingCart().add(product);
            }
            
            assertEquals(100, homepageController.getCartItemCount());
            assertEquals(1000000.0, homepageController.calculateCartTotalAsDouble(), 0.01);
        }
        
        @Test
        @DisplayName("Should handle genre counting with case sensitivity")
        void testGenreCaseSensitivity() {
            AllProduct popProduct = new AllProduct(1, "Pop Album", "Desc", new BigDecimal("100000"), "Pop", "CD", "test.png", 4.0f, 3);
            AllProduct POPProduct = new AllProduct(2, "POP Album", "Desc", new BigDecimal("100000"), "POP", "CD", "test.png", 4.0f, 3);
            
            homepageController.getShoppingCart().add(popProduct);
            homepageController.getShoppingCart().add(POPProduct);
            
            // Test exact case match
            assertEquals(1, homepageController.getCartProductCountByGenre("Pop"));
            assertEquals(1, homepageController.getCartProductCountByGenre("POP"));
            assertEquals(0, homepageController.getCartProductCountByGenre("pop"));
        }
    }
}