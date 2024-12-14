package org.example.classes;

public class Product {

    private int productID;
    private String productName;
    private int stock;
    private Double price;

    public Product(){}

    public Product(int productID, String productName, int stock, Double price){
        this.productID = productID;
        this.productName = productName;
        this.stock = stock;
        this.price = price;
    }

    public int getProductID() {
        return this.productID;
    }

    public String getProductName() {
        return this.productName;
    }

    public int getStock() {
        return this.stock;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
