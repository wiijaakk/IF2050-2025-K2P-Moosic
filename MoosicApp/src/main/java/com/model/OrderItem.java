package com.model;

import java.math.BigDecimal; 

public class OrderItem {

    private Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public double getSubtotal() {
        if (product == null || product.getPrice() == null) {
            return 0.0;
        }
        // Konversi quantity ke BigDecimal, lalu kalikan, dan ambil nilai double-nya
        return product.getPrice().multiply(new BigDecimal(this.quantity)).doubleValue();
    }
}