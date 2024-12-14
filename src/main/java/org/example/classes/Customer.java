package org.example.classes;

public class Customer {

    private int customerID;
    private String customerName;
    private Double budget;
    private String customerType;
    private Double totalSpent;

    public Customer(){}

    public Customer(int customerID, String customerName, Double budget, String customerType, Double totalSpent){
        this.customerID = customerID;
        this.customerName = customerName;
        this.budget = budget;
        this.customerType = customerType;
        this.totalSpent = totalSpent;
    }

    public int getCustomerID() {
        return this.customerID;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public Double getBudget() {
        return this.budget;
    }

    public String getCustomerType() {
        return this.customerType;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

}
