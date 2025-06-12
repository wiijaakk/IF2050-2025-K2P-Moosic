package com.model;

import com.database.ShopDAO;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShopModel {

    private final List<AllProduct> allProducts;
    private List<AllProduct> filteredProducts;
    private static final int ITEMS_PER_PAGE = 12;
    private int totalPages = 0;

    public ShopModel() {
        this.allProducts = ShopDAO.getAllProducts();
        this.filteredProducts = new ArrayList<>(this.allProducts);
        updatePagination();
    }
    public ShopModel(List<AllProduct> dummyData) {
        this.allProducts = new ArrayList<>(dummyData);
        this.filteredProducts = new ArrayList<>(this.allProducts);
        updatePagination();
    }

    public void applyFilterSearch(String genre, String variant, String userQuery) {
        Stream<AllProduct> productStream = allProducts.stream();

        if (genre != null && !genre.equals("Music Genre")) {
            productStream = productStream.filter(p -> p.getGenre().equalsIgnoreCase(genre));
        }

        if (variant != null && !variant.equals("Product Variant")) {
            productStream = productStream.filter(p -> p.getVariant().equalsIgnoreCase(variant));
        }

        if (userQuery != null && !userQuery.trim().isEmpty()) {
            String cleanQuery = userQuery.trim().toLowerCase();
            String escapedQuery = Pattern.quote(cleanQuery);
            String flexiblePattern = ".*" + escapedQuery + ".*";
            Pattern pattern = Pattern.compile(flexiblePattern, Pattern.CASE_INSENSITIVE);
            productStream = productStream.filter(p -> pattern.matcher(p.getName()).find());
        }

        this.filteredProducts = productStream.collect(Collectors.toList());
        this.filteredProducts.sort(Comparator.comparing(AllProduct::getName));

        updatePagination();
    }

    private void updatePagination() {
        if (this.filteredProducts.isEmpty()) {
            this.totalPages = 0;
        } else {
            this.totalPages = (int) Math.ceil((double) this.filteredProducts.size() / ITEMS_PER_PAGE);
        }
    }

    public List<AllProduct> getFilteredProducts() {
        return filteredProducts;
    }

    public List<AllProduct> getProductsForPage(int page) {
        int fromIndex = page * ITEMS_PER_PAGE;
        if (filteredProducts.isEmpty() || fromIndex >= filteredProducts.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredProducts.size());
        return filteredProducts.subList(fromIndex, toIndex);
    }

    public int getTotalPages() {
        return this.totalPages;
    }
}