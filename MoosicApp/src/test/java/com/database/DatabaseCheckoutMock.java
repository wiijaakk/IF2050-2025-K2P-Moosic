package com.database;

import com.model.DiscountPromo;
import com.model.Order;
import com.model.OrderItem;
import com.model.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class DatabaseCheckoutMock extends DatabaseCheckout {

    public static boolean placeOrder(Order order, int customerId) {
        return order != null && !order.getItems().isEmpty();
    }

    public static DiscountPromo validatePromoCode(String promoCode) {
        if (promoCode.equalsIgnoreCase("PROMO123")) {
            return new DiscountPromo(
                1, "Promo Diskon 10%", "PROMO123",
                new BigDecimal("10.0"), true,
                OffsetDateTime.now().minusDays(1), OffsetDateTime.now().plusDays(1)
            );
        }
        return null;
    }
}