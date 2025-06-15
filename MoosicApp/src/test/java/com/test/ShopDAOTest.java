package com.test;

import com.database.ShopDAO;
import com.model.AllProduct;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShopDAOTest {

    @Test
    public void testGetAllProducts_shouldReturnNonEmptyList() {
        List<AllProduct> products = ShopDAO.getAllProducts();
        assertNotNull(products, "Produk tidak boleh null");
        assertFalse(products.isEmpty(), "Produk tidak boleh kosong");

        AllProduct first = products.get(0);
        assertNotNull(first.getName());
        assertTrue(first.getPrice() > 0);
    }

    @Test
    public void testGetProductById_validId_shouldReturnCorrectProduct() {
        int validId = 1; 
        AllProduct product = ShopDAO.getProductById(validId);
        assertNotNull(product);
        assertEquals(validId, product.getId());
    }

    @Test
    public void testGetProductById_invalidId_shouldReturnNull() {
        int invalidId = -1;
        AllProduct product = ShopDAO.getProductById(invalidId);
        assertNull(product);
    }
}
