package com.test;

import com.model.AllProduct;
import com.model.ShopModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShopModelTest {

    private ShopModel model;

    @BeforeEach
    public void setUp() {
        model = new ShopModel();
    }

    @Test
    public void testApplyFilterSearch_shouldReturnFilteredResults() {
        model.applyFilterSearch("Pop", "Vinyl", "Midnights");
        List<AllProduct> filtered = model.getFilteredProducts();

        assertNotNull(filtered, "Hasil filter tidak boleh null");
        assertFalse(filtered.isEmpty(), "Hasil filter tidak boleh kosong");

        for (AllProduct p : filtered) {
            assertTrue(p.getGenre().toLowerCase().contains("pop"));
            assertTrue(p.getVariant().toLowerCase().contains("vinyl"));
            assertTrue(p.getName().toLowerCase().contains("midnights"));
        }
    }
}
