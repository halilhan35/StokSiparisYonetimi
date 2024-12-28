package org.example.threads;

import org.example.Application;
import org.example.classes.Customer;
import org.example.classes.Log;
import org.example.classes.Order;
import org.example.classes.Product;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderThread extends Thread {
    private String customerName;
    private String productName;
    private int quantity;
    private Connection connection;

    private static ArrayList<Product> productList = new ArrayList<Product>();
    private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    private static ArrayList<Log> logList = new ArrayList<Log>();
    private static ArrayList<Order> orderList = new ArrayList<Order>();

    private ArrayList<Order> preCustomers = new ArrayList<Order>();
    private ArrayList<Order> staCustomers = new ArrayList<Order>();

    private static boolean stopFlag = false;

    public OrderThread(String customerName, String productName, int quantity, Connection connection) {
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            synchronized (connection) {

                if (stopFlag) {
                    return; // Thread'i durdurur
                }

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = now.format(formatter);

                productList = Application.getProductList();
                customerList = Application.getCustomerList();
                logList = Application.getLogList();
                orderList = Application.getOrderList();

                // Müşteri bilgilerini kontrol et
                String customerQuery = "SELECT * FROM customers WHERE customername = ?";
                PreparedStatement customerStmt = connection.prepareStatement(customerQuery);
                customerStmt.setString(1, customerName);
                ResultSet customerRs = customerStmt.executeQuery();

                if (!customerRs.next()) {
                    JOptionPane.showMessageDialog(null, "Müşteri bulunamadı: " + customerName, "Başarısız", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int customerID = customerRs.getInt("customerid");

                // Ürün bilgilerini kontrol et
                String productQuery = "SELECT * FROM products WHERE productname = ?";
                PreparedStatement productStmt = connection.prepareStatement(productQuery);
                productStmt.setString(1, productName);
                ResultSet productRs = productStmt.executeQuery();

                if (!productRs.next()) {
                    JOptionPane.showMessageDialog(null, "Ürün bulunamadı: " + productName, "Başarısız", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int productID = productRs.getInt("productid");
                double price = productRs.getDouble("price");
                double totalCost = quantity * price;

                int orderID;
                // Siparişi orders tablosuna ekle
                String orderQuery = "INSERT INTO orders (customerid, productid, quantity, totalprice, orderdate, orderstatus, confirm) VALUES (?, ?, ?, ?,?, ?, ?) RETURNING orderid";
                PreparedStatement orderStmt = connection.prepareStatement(orderQuery);
                orderStmt.setInt(1, customerID);
                orderStmt.setInt(2, productID);
                orderStmt.setInt(3, quantity);
                orderStmt.setDouble(4, totalCost);
                orderStmt.setString(5,formattedDate);
                orderStmt.setString(6, "Bekliyor");
                orderStmt.setBoolean(7, false);
                ResultSet orderRs = orderStmt.executeQuery();

                if (orderRs.next()) {
                    orderID = orderRs.getInt("orderid");
                } else {
                    JOptionPane.showMessageDialog(null, "Sipariş eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Order order = new Order(orderID, customerID, productID, quantity, totalCost, formattedDate, "Bekliyor.", false);
                orderList.add(order);

                for (Order order1 : orderList) {
                    for (Customer customer : customerList) {
                        if(order1.getCustomerID() == customer.getCustomerID()) {
                            if (customer.getCustomerType().equals("Premium")) {
                                preCustomers.add(order1);
                            } else if (customer.getCustomerType().equals("Standard")) {
                                staCustomers.add(order1);
                            }
                        }
                    }
                }

                orderList.clear();

                orderList.addAll(preCustomers);
                orderList.addAll(staCustomers);

                preCustomers.clear();
                staCustomers.clear();

                Application.setOrderList(orderList);
                Application.getMainPage().updateOrdersTable();
                if(Application.getAdminPage().getOrderTable() != null) {
                    Application.getAdminPage().updateOrdersTable();
                }

                // Log kaydı ekle
                String logQuery = "INSERT INTO logs (customerid, orderid, logdate, logtype, logdetails) VALUES (?, ?, ?, 'Sipariş', ?)";
                PreparedStatement logStmt = connection.prepareStatement(logQuery);
                logStmt.setInt(1, customerID);
                logStmt.setInt(2, orderID);
                logStmt.setString(3,formattedDate);
                logStmt.setString(4, customerName + ", " + productName + "'den " + quantity + " adet sipariş verdi.");

                logStmt.executeUpdate();

                logList.clear();

                try {
                    String query = "SELECT * FROM logs";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int logIDk = rs.getInt("logid");
                        int customerIDk = rs.getInt("customerid");
                        int orderIDk = rs.getInt("orderid");
                        String logDate = rs.getString("logdate");
                        String logType = rs.getString("logtype");
                        String logDetails = rs.getString("logdetails");

                        Log log = new Log(logIDk, customerIDk, orderIDk, logDate, logType, logDetails);
                        logList.add(log);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Application.setLogList(logList);
                if(Application.getLogPage().getLogTable() != null) {
                    Application.getLogPage().updateLogsTable();
                }

                System.out.println();
                JOptionPane.showMessageDialog(null, "Sipariş verildi: " + customerName + " -> " + productName + " (" + quantity + " adet)", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stopThread() {
        stopFlag = true;
    }

    public static void resetStopFlag() {
        stopFlag = false;
    }

}
