package org.example;
import org.example.classes.*;
import org.example.database.ConnectDB;
import org.example.generator.CustomerGenerator;
import org.example.pages.AdminPage;
import org.example.pages.LogPage;
import org.example.pages.LoginPage;
import org.example.pages.MainPage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static Connection connection;

    private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    private static ArrayList<Product> productList = new ArrayList<Product>();
    private static ArrayList<Log> logList = new ArrayList<Log>();
    private static ArrayList<Order> orderList = new ArrayList<Order>();

    private static MainPage mainPage;
    private static LoginPage loginPage;
    private static AdminPage adminPage;
    private static LogPage logPage;

    public static void main(String[] args) {

        // veritabanını ürünler hariç güzelce temizleyip
        // rastgele 5-10 arası kişi eklemeliyiz.

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection = ConnectDB.getConnection();

        String query = "DELETE FROM orders";  // orders tablosundaki tüm verileri siler
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int affectedRows = stmt.executeUpdate();  // Veritabanındaki satırları siler
            // System.out.println(affectedRows + " rows deleted from the orders table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query2 = "DELETE FROM logs";  // logs tablosundaki tüm verileri siler
        try (PreparedStatement stmt = connection.prepareStatement(query2)) {
            int affectedRows = stmt.executeUpdate();  // Veritabanındaki satırları siler
           //  System.out.println(affectedRows + " rows deleted from the logs table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query3 = "DELETE FROM customers";  // logs tablosundaki tüm verileri siler
        try (PreparedStatement stmt = connection.prepareStatement(query3)) {
            int affectedRows = stmt.executeUpdate();  // Veritabanındaki satırları siler
            //  System.out.println(affectedRows + " rows deleted from the customers table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Customer> customers = CustomerGenerator.generateCustomers();

        String query4 = "INSERT INTO customers (customerid, customername, budget, customertype, totalspent,password) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query4)) {
            for (Customer customer : customers) {
                stmt.setInt(1, customer.getCustomerID());
                stmt.setString(2, customer.getCustomerName());
                stmt.setDouble(3, customer.getBudget());
                stmt.setString(4, customer.getCustomerType());
                stmt.setDouble(5, customer.getTotalSpent());
                stmt.setString(6, customer.getCustomerPassword());

                stmt.addBatch();
            }

            stmt.executeBatch();
           // System.out.println("Customers added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerList.clear();
        productList.clear();
        logList.clear();
        orderList.clear();

        selectAllCustomers();
        selectAllProducts();
        selectAllLogs();
        selectOrders();

        mainPage = new MainPage();
        mainPage.createAndShowGUI();
        mainPage.getFrame().dispose();

        adminPage = new AdminPage();
        adminPage.createAndShowGUI();
        adminPage.getFrame().dispose();

        logPage = new LogPage();
        logPage.createAndShowGUI();
        logPage.getFrame().dispose();

        loginPage = new LoginPage();
        loginPage.createAndShowGUI();

    }

    public static void selectAllCustomers() {
        try {
            String query = "SELECT * FROM customers";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int customerID = rs.getInt("customerid");
                String customerName = rs.getString("customername");
                double budget = rs.getDouble("budget");
                String customerType = rs.getString("customertype");
                double totalSpent = rs.getDouble("totalspent");

                Customer customer = new Customer(customerID,customerName,"",budget,customerType,totalSpent);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectAllProducts() {
        try {
            String query = "SELECT * FROM products";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productID = rs.getInt("productid");
                String productName = rs.getString("productname");
                int stock = rs.getInt("stock");
                double price = rs.getDouble("price");

                Product product = new Product(productID,productName,stock,price);
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectAllLogs(){
        try {
            String query = "SELECT * FROM logs";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int logID = rs.getInt("logid");
                int customerID = rs.getInt("customerid");
                int orderID = rs.getInt("orderid");
                String logDate = rs.getString("logdate");
                String logType = rs.getString("logtype");
                String logDetails = rs.getString("logdetails");

                Log log = new Log(logID,customerID,orderID,logDate,logType,logDetails);
                logList.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void selectOrders(){
        try {
            String query = "SELECT * FROM orders WHERE confirm = false";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderID = rs.getInt("orderid");
                int customerID = rs.getInt("customerid");
                int productID = rs.getInt("productid");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("totalprice");
                String orderDate = rs.getString("orderdate");
                String orderStatus = rs.getString("orderstatus");
                boolean confirm = rs.getBoolean("confirm");

                Order order = new Order(orderID,customerID, productID, quantity, totalPrice, orderDate, orderStatus, confirm);
                orderList.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MainPage getMainPage() {
        return mainPage;
    }

    public static LoginPage getLoginPage() {
        return loginPage;
    }

    public static AdminPage getAdminPage() {
        return adminPage;
    }

    public static LogPage getLogPage() {
        return logPage;
    }

    public static ArrayList<Product> getProductList() {
        return Application.productList;
    }

    public static ArrayList<Customer> getCustomerList() {
        return Application.customerList;
    }

    public static ArrayList<Order> getOrderList() {
        return Application.orderList;
    }

    public static void setProductList(ArrayList<Product> productList) {
        Application.productList = productList;
    }

    public static void setCustomerList(ArrayList<Customer> customerList) {
        Application.customerList = customerList;
    }

    public static void setOrderList(ArrayList<Order> orderList) {
        Application.orderList = orderList;
    }

    public static ArrayList<Log> getLogList() {
        return logList;
    }

    public static void setLogList(ArrayList<Log> logList) {
        Application.logList = logList;
    }

}
