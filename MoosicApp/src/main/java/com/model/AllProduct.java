package com.model;

import java.math.BigDecimal;
import java.util.List;

public class AllProduct {
    private int id_produk;
    private String nama_produk;
    private String deskripsi;
    private BigDecimal harga;
    private String genre;
    private String varian;
    private String gambar;
    private Float rating;
    private int terjual;


    public AllProduct(int id_produk, String nama_produk, String deskripsi, BigDecimal harga, String genre, String varian, String gambar, Float rating, int terjual) {
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.genre = genre;
        this.varian = varian;
        this.gambar = gambar;
        this.rating = rating;
        this.terjual = terjual;
    }
    
    public int getId() { return id_produk; }
    public String getName() { return nama_produk; }
    public double getPrice() { return harga.doubleValue(); }
    public String getDescription() { return deskripsi; }
    public String getGenre() { return genre; }
    public String getVariant() { return varian; }
    public String getImage() { return gambar; }
    public Float getRating() { return rating; }
    public int getTerjual() { return terjual; }
}