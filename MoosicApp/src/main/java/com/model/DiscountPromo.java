package com.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class DiscountPromo {
    private int id_promo;
    private String nama_promo;
    private String promo_code;
    private BigDecimal persentase;
    private boolean berlaku;
    private OffsetDateTime valid_from;
    private OffsetDateTime valid_until;

    public DiscountPromo(int id_promo, String nama_promo, String promo_code, BigDecimal persentase, boolean berlaku, OffsetDateTime valid_from, OffsetDateTime valid_until) {
        this.id_promo = id_promo;
        this.nama_promo = nama_promo;
        this.promo_code = promo_code;
        this.persentase = persentase;
        this.berlaku = berlaku;
        this.valid_from = valid_from;
        this.valid_until = valid_until;
    }

    // Getters
    public int getId_promo() { return id_promo; }
    public String getNama_promo() { return nama_promo; }
    public String getPromo_code() { return promo_code; }
    public BigDecimal getPersentase() { return persentase; }
    public boolean isBerlaku() { return berlaku; }
    public OffsetDateTime getValid_from() { return valid_from; }
    public OffsetDateTime getValid_until() { return valid_until; }
}