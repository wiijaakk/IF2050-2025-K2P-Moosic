package com.database;

import com.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseCheckoutTest {

    @Test
    void testPlaceOrderSuccess() {
        Order order = createDummyOrder();
        boolean result = DatabaseCheckoutMock.placeOrder(order, 1);
        assertTrue(result);
    }

    @Test
    void testPlaceOrderFailsIfOrderEmpty() {
        Order order = new Order(); // tidak ada item
        boolean result = DatabaseCheckoutMock.placeOrder(order, 1);
        assertFalse(result); // Harusnya gagal
    }

    @Test
    void testPromoValid() {
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("PROMO123");
        assertNotNull(promo);
        assertEquals("PROMO123", promo.getPromo_code());
    }

    @Test
    void testPromoInvalid() {
        DiscountPromo promo = DatabaseCheckoutMock.validatePromoCode("GAGAL");
        assertNull(promo);
    }

    private Order createDummyOrder() {
        Product p = new Product(1, "Vinyl", "Desc", new BigDecimal("100000"), "Kategori", new ArrayList<ProductReview>());
        OrderItem item = new OrderItem(p, 1);
        Order order = new Order();
        order.addItem(item);
        order.setShippingName("Nama");
        order.setShippingAddress("Alamat");
        order.setShippingCity("Kota");
        order.setShippingCountry("Negara");
        order.setShippingState("Provinsi");
        order.setShippingZipCode("12345");
        order.setShippingMobilePhone("08123456789");
        order.setCardFullName("Nama Kartu");
        order.setCardNumber("1234123412341234");
        order.setCardExpirationDate("12/25");
        order.setCardCvv("123");
        return order;
    }
}