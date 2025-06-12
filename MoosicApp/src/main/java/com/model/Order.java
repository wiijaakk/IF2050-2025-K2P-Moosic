package com.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String shippingName;
    private String shippingMobilePhone;
    private String shippingAddress;
    private String shippingCountry;
    private String shippingState;
    private String shippingCity;
    private String shippingZipCode;

    private String cardFullName;
    private String cardNumber;
    private String cardExpirationDate; // Format "MM/YY"
    private String cardCvv;

    private List<OrderItem> items;
    private String promoCode;
    private double shippingCost;
    private double discount;
    private double tax;

    public Order() {
        this.items = new ArrayList<>();
        this.shippingCost = 0.0; 
        this.discount = 0.0;
        this.tax = 0.0;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (OrderItem item : this.items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double calculateTotal() {
        return calculateSubtotal() + this.shippingCost - this.discount + this.tax;
    }

    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }

    public String getShippingMobilePhone() { return shippingMobilePhone; }
    public void setShippingMobilePhone(String shippingMobilePhone) { this.shippingMobilePhone = shippingMobilePhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }

    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingZipCode() { return shippingZipCode; }
    public void setShippingZipCode(String shippingZipCode) { this.shippingZipCode = shippingZipCode; }

    public String getCardFullName() { return cardFullName; }
    public void setCardFullName(String cardFullName) { this.cardFullName = cardFullName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpirationDate() { return cardExpirationDate; }
    public void setCardExpirationDate(String cardExpirationDate) { this.cardExpirationDate = cardExpirationDate; }

    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
    
    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
}