package com.gamezone.model;

import java.util.List;

public class Sale {
    private int id;
    private String date;
    private Client client;
    private Seller seller;
    private List<Product> products; 
    private double total;

    public Sale() {
    }

    public Sale(int id, String date, Client client, Seller seller, List<Product> products) {
        this.id = id;
        this.date = date;
        this.client = client;
        this.seller = seller;
        this.products = products;
        this.total = calculateTotal();
    }

    public double calculateTotal() {
        double sum = 0.0;
        if (products != null) {
            for (Product product : products) {
                // Asumiendo que Product tiene un método getPrice()
                sum += product.getPrice();
            }
        }
        this.total = sum;
        return this.total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) {
        this.products = products;
        this.total = calculateTotal(); 
    }

    public double getTotal() { return total; }
}