package org.example.classes;

public class Order {

    private int orderID;
    private int customerID;
    private int productID;
    private int quantity;
    private Double totalPrice;
    private String orderDate;
    private String orderStatus;
    private boolean confirm;
    private double priority;

    public Order(){}

    public Order(int orderID, int customerID, int productID, int quantity, Double totalPrice, String orderDate, String orderStatus, boolean confirm){
        this.orderID = orderID;
        this.customerID = customerID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.confirm = confirm;
    }

    public int getOrderID() {
        return this.orderID;
    }

    public int getCustomerID() {
        return this.customerID;
    }

    public int getProductID() {
        return this.productID;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Double getTotalPrice() {
        return this.totalPrice;
    }

    public String getOrderDate() {
        return this.orderDate;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public boolean getConfirm() {
        return confirm;
    }

    public double getPriority() {
        return priority;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

}
