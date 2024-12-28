package org.example.threads;

import org.example.Application;
import org.example.classes.Customer;
import org.example.classes.Log;
import org.example.classes.Order;
import org.example.classes.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControllerThread extends Thread {

    private boolean running;
    private Connection connection;
    private long startTime;
    private String formattedDate;
    private static final long TIMEOUT = 15000;
    private List<Order> orderList;
    private List<Customer> customerList;
    private List<Product> productList;
    public ControllerThread(List<Order> orderList, Connection connection) {
        this.running = true;
        this.orderList = orderList;
        this.connection = connection; // Listeyi Constructor'dan alıyoruz
    }

    @Override
    public void run() {
        System.out.println("ControllerThread başladı...");
        startTime = System.currentTimeMillis();

        customerList = Application.getCustomerList();
        productList = Application.getProductList();

        while (running) {
            System.out.println("ControllerThread running...");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formattedDate = now.format(formatter);

            try {
                processPendingOrders();  // Siparişleri işleme

                if (allOrdersConfirmed()) {
                    System.out.println("Tüm siparişler onaylandı. Thread durduruluyor.");
                    stopThread();
                }

                if (System.currentTimeMillis() - startTime > TIMEOUT) {
                    handleTimeoutOrders();  // Zaman aşımını kontrol etme
                    stopThread();
                }

                Thread.sleep(1000);  // Thread'i 1 saniye beklet
            } catch (InterruptedException e) {
                System.out.println("Thread kesildi: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Veritabanı hatası: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processPendingOrders() {
        for (Order order : orderList) {
            if (!order.getConfirm()) {  // Sipariş onaylanmamışsa
                System.out.println("İşleniyor: Sipariş ID: " + order.getOrderID());
                handleOrder(order);  // Siparişi işleme
            }
        }
    }

    private void handleOrder(Order order) {
        int orderID = order.getOrderID();
        int customerID = order.getCustomerID();
        int productID = order.getProductID();
        int quantity = order.getQuantity();
        double totalPrice = order.getTotalPrice();

        try {
            // Stok kontrolü
            if (!isStockAvailable(productID, quantity)) {
                System.out.println("Stok yetersiz: Sipariş ID " + orderID);
                order.setConfirm(true); // Liste üzerindeki siparişi onayla
                updateOrderInDatabase(orderID, true, "İşlendi");
                logUpdate(orderID, customerID, productID, quantity, "Stok Durumu", "Sipariş başarısız: Ürün stoğu yetersiz.");
                return;
            }

            // Bütçe kontrolü
            if (!isBudgetSufficient(customerID, totalPrice)) {
                System.out.println("Bütçe yetersiz: Sipariş ID " + orderID);
                order.setConfirm(true); // Liste üzerindeki siparişi onayla
                updateOrderInDatabase(orderID, true, "İşlendi"); // Veritabanında siparişi güncelle
                logUpdate(orderID, customerID, productID, quantity, "Bütçe Durumu", "Sipariş başarısız: Bütçe yetersiz.");
                return;
            }

            // Sipariş onaylama
            updateStock(productID, quantity);
            updateCustomerBudget(customerID, totalPrice);
            order.setConfirm(true); // Liste üzerindeki siparişi onayla
            updateOrderInDatabase(orderID, true, "İşlendi"); // Veritabanında siparişi güncelle
            System.out.println("Sipariş onaylandı: Sipariş ID " + orderID);
            logUpdate(orderID, customerID, productID, quantity, "Sipariş Onayı", "Sipariş başarıyla onaylandı.");
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası (handleOrder): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isStockAvailable(int productID, int quantity) throws SQLException {
        String query = "SELECT stock FROM products WHERE productid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productID);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock");
                return stock >= quantity;
            }
        }
        return false;
    }

    private boolean isBudgetSufficient(int customerID, double totalPrice) throws SQLException {
        String query = "SELECT budget FROM customers WHERE customerid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                double budget = rs.getDouble("budget");
                return budget >= totalPrice;
            }
        }
        return false;
    }

    private void updateStock(int productID, int quantity) throws SQLException {
        String query = "UPDATE products SET stock = stock - ? WHERE productid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productID);
            stmt.executeUpdate();
            System.out.println("Stok güncellendi: ÜrünID " + productID + ", Miktar " + quantity);
        }
    }

    private void updateCustomerBudget(int customerID, double totalPrice) throws SQLException {
        String query = "UPDATE customers SET budget = budget - ?, totalspent = totalspent + ? WHERE customerid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, totalPrice);
            stmt.setDouble(2, totalPrice);
            stmt.setInt(3, customerID);
            stmt.executeUpdate();
            System.out.println("Bütçe güncellendi: MüşteriID " + customerID + ", Tutar " + totalPrice);
        }
    }

    private void logUpdate(int orderID, int customerID, int productID, int quantity, String logType, String message) throws SQLException {
        String customerName = getCustomerNameByID(customerID);

        String productName = getProductNameByID(productID);

        String logDetails = String.format("%s, %s'den %d adet sipariş etmişti. %s", customerName, productName, quantity, message);

        int logID = generateLogID();
        try {
            String insertLogQuery = "INSERT INTO logs (logid, customerid, orderid, logdate, logtype, logdetails) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertLogQuery)) {
                stmt.setInt(1, logID);
                stmt.setInt(2, customerID);  // customerID'yi yine de veritabanına kaydediyoruz
                stmt.setInt(3, orderID);
                stmt.setString(4, formattedDate);
                stmt.setString(5, logType);
                stmt.setString(6, logDetails);
                int rowsAffected = stmt.executeUpdate();

                Log log = new Log(logID, customerID, orderID, formattedDate, logType, logDetails);
                Application.getLogList().add(log);
                Application.getLogPage().updateLogsTable();

                if (rowsAffected > 0) {
                    System.out.println("Log başarıyla eklendi: " + logDetails);
                } else {
                    System.out.println("Log eklenemedi, satır etkilenmedi: " + logDetails);
                }
            }
        } catch (SQLException e) {
            System.out.println("Log ekleme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateOrderInDatabase(int orderID, boolean confirm, String orderType) throws SQLException {
        String updateQuery = "UPDATE orders SET confirm = ?, orderstatus = ? WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setBoolean(1, confirm);  // Sipariş onay durumunu güncelle
            stmt.setString(2, orderType); // Sipariş tipini güncelle
            stmt.setInt(3, orderID);      // Sipariş ID'si
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Sipariş başarıyla güncellendi: Sipariş ID " + orderID);
            } else {
                System.out.println("Sipariş güncellenemedi: Sipariş ID " + orderID);
            }
        }
    }

    private String getCustomerNameByID(int customerID) {
        for (Customer customer : customerList) {
            if (customer.getCustomerID() == customerID) {
                return customer.getCustomerName();  // Müşteri ismini döndürüyoruz
            }
        }
        return "Bilinmeyen Müşteri";  // Eğer müşteri bulunamazsa
    }

    private String getProductNameByID(int productID) {
        for (Product product : productList) {
            if (product.getProductID() == productID) {
                return product.getProductName();  // Ürün ismini döndürüyoruz
            }
        }
        return "Bilinmeyen Ürün";  // Eğer ürün bulunamazsa
    }


    private int generateLogID() throws SQLException {
        String query = "SELECT MAX(logid) AS max_logid FROM logs";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_logid") + 1;
            }
        }
        return 1;
    }

    private void handleTimeoutOrders() throws SQLException {
        for (Order order : orderList) {
            if (!order.getConfirm()) {
                System.out.println("Zaman aşımına uğramış sipariş ID: " + order.getOrderID());
                logUpdate(order.getOrderID(), order.getCustomerID(), order.getProductID(), order.getQuantity(),
                        "Zaman Aşımı", "Sipariş işlenemedi: Zaman Aşımı.");
            }
        }
    }

    public void stopThread() {
        this.running = false;
        System.out.println("Thread durduruldu.");
    }

    private boolean allOrdersConfirmed() {
        for (Order order : orderList) {
            if (!order.getConfirm()) {
                return false;  // Eğer onaylanmamış bir sipariş varsa false döndür
            }
        }
        return true;  // Tüm siparişler onaylanmışsa true döndür
    }

}
