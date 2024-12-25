package org.example.pages;

import org.example.classes.Customer;
import org.example.classes.Order;
import org.example.classes.Product;
import org.example.classes.Log;
import org.example.database.ConnectDB;
import org.example.threads.OrderThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MainPage {

    private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    private static ArrayList<Product> productList = new ArrayList<Product>();
    private static ArrayList<Log> logList = new ArrayList<Log>();
    private static ArrayList<Order> orderList = new ArrayList<Order>();
    private ArrayList<Order> preCustomers = new ArrayList<Order>();
    private ArrayList<Order> staCustomers = new ArrayList<Order>();

    private static JButton orderButton = new JButton("Sipariş Ver");

    private Connection connection;

    private JFrame frame = new JFrame("Products Orders App");
    private static JTable productTable;
    private static JTable customerTable;
    private static JTable orderTable;

    public void createAndShowGUI() {

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection = ConnectDB.getConnection();

        selectAllCustomers();
        selectAllProducts();
        selectAllLogs();
        selectOrders();

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

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Background panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage image = ImageIO.read(new File("src/main/java/org/example/drawables/background.png"));
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        panel.setLayout(null);
        frame.setContentPane(panel);

        // Customer List Label
        JLabel customerListLabel = new JLabel("Müşteri Listesi");
        customerListLabel.setBounds(53, 468, 240, 40);
        customerListLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        customerListLabel.setForeground(Color.WHITE);
        panel.add(customerListLabel);

        // Product Table Label
        JLabel productTableLabel = new JLabel("Ürün Tablosu");
        productTableLabel.setBounds(399, 30, 240, 40);
        productTableLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productTableLabel.setForeground(Color.WHITE);
        productTableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(productTableLabel);

        // Order Queue Label
        JLabel orderQueueLabel = new JLabel("Sipariş Sırası");
        orderQueueLabel.setBounds(780, 30, 240, 40);
        orderQueueLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        orderQueueLabel.setForeground(Color.WHITE);
        orderQueueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(orderQueueLabel);

        // Customer List Rectangle
        JPanel customerListRectangle = new JPanel();
        customerListRectangle.setBounds(50, 530, 1100, 240);
        customerListRectangle.setBackground(new Color(0x767676));
        panel.add(customerListRectangle);

        // Customer List Table
        String[] customerColumnNames = {"Adı", "Türü", "Bütçe", "Toplam Harcama"};
        Object[][] customerData = new Object[customerList.size()][4];

        // Fill the data array with customerList content
        for (int i = 0; i < customerList.size(); i++) {
            Customer customer = customerList.get(i);
            customerData[i][0] = customer.getCustomerName();
            customerData[i][1] = customer.getCustomerType();
            customerData[i][2] = customer.getBudget();
            customerData[i][3] = customer.getTotalSpent();
        }

        DefaultTableModel customerTableModel = new DefaultTableModel(customerData, customerColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        customerTable = new JTable(customerTableModel);
        customerTable.setFont(new Font("Inter", Font.PLAIN, 16));
        customerTable.setRowHeight(30);
        customerTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        customerTable.getTableHeader().setBackground(new Color(0x555555));
        customerTable.getTableHeader().setForeground(Color.WHITE);
        customerTable.setBackground(new Color(0xE0E0E0));
        customerTable.setForeground(Color.BLACK);

       // Add the table to a scroll pane
        JScrollPane scrollPaneCustomer = new JScrollPane(customerTable);
        scrollPaneCustomer.setBounds(0, 0, customerListRectangle.getWidth(), customerListRectangle.getHeight());
        scrollPaneCustomer.setBackground(new Color(0x767676));
        scrollPaneCustomer.setBorder(BorderFactory.createEmptyBorder());

       // Add the scroll pane to the rectangle
        customerListRectangle.setLayout(null);
        customerListRectangle.add(scrollPaneCustomer);

        // Customer Name Label
        JLabel customerNameLabel = new JLabel("Müşteri Adı:");
        customerNameLabel.setBounds(50, 30, 240, 40);
        customerNameLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        customerNameLabel.setForeground(Color.WHITE);
        panel.add(customerNameLabel);

        // Product Name Label
        JLabel productNameLabel = new JLabel("Ürün Adı:");
        productNameLabel.setBounds(50, 140, 240, 40);
        productNameLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productNameLabel.setForeground(Color.WHITE);
        panel.add(productNameLabel);

        // Product Number Label
        JLabel productNumberLabel = new JLabel("Ürün Miktarı:");
        productNumberLabel.setBounds(50, 250, 240, 40);
        productNumberLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productNumberLabel.setForeground(Color.WHITE);
        panel.add(productNumberLabel);

        // Customer Name TextField
        JTextField customerNameField = new JTextField();
        customerNameField.setBounds(50, 80, 250, 50);
        customerNameField.setBackground(new Color(0xE0E0E0));
        customerNameField.setFont(new Font("Inter", Font.PLAIN, 20));
        customerNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(customerNameField);

        // Product Name TextField
        JTextField productNameField = new JTextField();
        productNameField.setBounds(50, 190, 250, 50);
        productNameField.setBackground(new Color(0xE0E0E0));
        productNameField.setFont(new Font("Inter", Font.PLAIN, 20));
        productNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(productNameField);

        // Product Number TextField
        JTextField productNumberField = new JTextField();
        productNumberField.setBounds(50, 300, 250, 50);
        productNumberField.setBackground(new Color(0xE0E0E0));
        productNumberField.setFont(new Font("Inter", Font.PLAIN, 20));
        productNumberField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(productNumberField);

        // Order Button
        orderButton.setEnabled(true);
        orderButton.setBounds(53, 380, 240, 40);
        orderButton.setBackground(new Color(0xE0E0E0));
        orderButton.setForeground(Color.BLACK);
        orderButton.setFont(new Font("Inter", Font.PLAIN, 24));
        orderButton.setOpaque(true);
        orderButton.setContentAreaFilled(true);
        orderButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(orderButton);

        orderButton.addActionListener(e -> {
            String customerName = customerNameField.getText().trim();
            String productName = productNameField.getText().trim();
            int quantity;

            try {
                quantity = Integer.parseInt(productNumberField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Lütfen geçerli bir miktar girin!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (customerName.isEmpty() || productName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OrderThread orderThread = new OrderThread(customerName, productName, quantity, connection);
            orderThread.start();
        });

        // Product Table Panel
        JPanel productTablePanel = new JPanel();
        productTablePanel.setBounds(359, 105, 320, 360);
        productTablePanel.setBackground(new Color(0x767676));
        panel.add(productTablePanel);

        String[] productColumnNames = {"Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[productList.size()][3];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductName();
            productData[i][1] = product.getStock();
            productData[i][2] = product.getPrice();
        }

        DefaultTableModel productTableModel = new DefaultTableModel(productData, productColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setFont(new Font("Inter", Font.PLAIN, 16));
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        productTable.getTableHeader().setBackground(new Color(0x555555));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setBackground(new Color(0xE0E0E0));
        productTable.setForeground(Color.BLACK);

        JScrollPane scrollPaneProduct = new JScrollPane(productTable);
        scrollPaneProduct.setBounds(0, 0, productTablePanel.getWidth(), productTablePanel.getHeight());
        scrollPaneProduct.setBackground(new Color(0x767676));
        scrollPaneProduct.setBorder(BorderFactory.createEmptyBorder());

        // Add the scroll pane to the rectangle
        productTablePanel.setLayout(null);
        productTablePanel.add(scrollPaneProduct);

        // Order Queue Panel
        JPanel orderQueuePanel = new JPanel();
        orderQueuePanel.setBounds(738, 105, 395, 360);
        orderQueuePanel.setBackground(new Color(0x767676));
        panel.add(orderQueuePanel);

        String[] orderColumnNames = {"Müşteri Türü", "Müşteri Adı", "Harcama"};
        Object[][] orderData = new Object[orderList.size()][3];

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            Customer customer = null;

            for(int j = 0 ; j < customerList.size(); j++){
                if(order.getCustomerID() == customerList.get(j).getCustomerID()){
                    customer = customerList.get(j);
                }
            }

            orderData[i][0] = customer.getCustomerType();
            orderData[i][1] = customer.getCustomerName();
            orderData[i][2] = order.getTotalPrice();

        }

        DefaultTableModel orderTableModel = new DefaultTableModel(orderData, orderColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setFont(new Font("Inter", Font.PLAIN, 16));
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        orderTable.getTableHeader().setBackground(new Color(0x555555));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setBackground(new Color(0xE0E0E0));
        orderTable.setForeground(Color.BLACK);

        JScrollPane scrollPaneOrder = new JScrollPane(orderTable);
        scrollPaneOrder.setBounds(0, 0, orderQueuePanel.getWidth(), orderQueuePanel.getHeight());
        scrollPaneOrder.setBackground(new Color(0x767676));
        scrollPaneOrder.setBorder(BorderFactory.createEmptyBorder());

        // Add the scroll pane to the rectangle
        orderQueuePanel.setLayout(null);
        orderQueuePanel.add(scrollPaneOrder);

        // Admin Icon
        JLabel adminIcon = new JLabel();
        adminIcon.setBounds(1118, 18, 64, 64);
        try {
            BufferedImage adminImage = ImageIO.read(new File("src/main/java/org/example/drawables/admin.png"));
            adminIcon.setIcon(new ImageIcon(adminImage.getScaledInstance(adminIcon.getWidth(), adminIcon.getHeight(), Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        panel.add(adminIcon);

        adminIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                AdminPage adminPage = new AdminPage();
                adminPage.createAndShowGUI();
            }
        });

        // Log Icon
        JLabel logIcon = new JLabel();
        logIcon.setBounds(1040, 21, 56, 56);
        try {
            BufferedImage logImage = ImageIO.read(new File("src/main/java/org/example/drawables/log.png"));
            logIcon.setIcon(new ImageIcon(logImage.getScaledInstance(adminIcon.getWidth(), adminIcon.getHeight(), Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        panel.add(logIcon);

        logIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                LogPage logPage = new LogPage();
                logPage.createAndShowGUI();
            }
        });

        // Frame properties
        frame.setSize(1200, 840);
        frame.setVisible(true);
    }

    public void selectAllCustomers() {
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

                Customer customer = new Customer(customerID,customerName,budget,customerType,totalSpent);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectAllProducts() {
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

    public void selectAllLogs(){
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

    public void selectOrders(){
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

    public static void updateProductTable() {
        String[] productColumnNames = {"Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[productList.size()][3];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductName();
            productData[i][1] = product.getStock();
            productData[i][2] = product.getPrice();
        }

        // Recreate the table model with updated data
        DefaultTableModel productTableModel = new DefaultTableModel(productData, productColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        // Update the table with the new model
        productTable.setModel(productTableModel);
        productTable.revalidate();  // Revalidate the table to reflect changes
        productTable.repaint();  // Repaint to ensure UI refresh
    }

    public static void updateOrdersTable() {
        String[] orderColumnNames = {"Müşteri Türü", "Müşteri Adı", "Harcama"};
        Object[][] orderData = new Object[orderList.size()][3];

        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            Customer customer = null;

            for(int j = 0 ; j < customerList.size(); j++){
                if(order.getCustomerID() == customerList.get(j).getCustomerID()){
                    customer = customerList.get(j);
                }
            }

            orderData[i][0] = customer.getCustomerType();
            orderData[i][1] = customer.getCustomerName();
            orderData[i][2] = order.getTotalPrice();

        }

        // Recreate the table model with updated data
        DefaultTableModel orderTableModel = new DefaultTableModel(orderData, orderColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Update the table with the new model
        orderTable.setModel(orderTableModel);
        orderTable.revalidate();  // Revalidate the table to reflect changes
        orderTable.repaint();  // Repaint to ensure UI refresh
    }

    public static void updateCustomersTable() {
        // Customer List Table
        String[] customerColumnNames = {"Adı", "Türü", "Bütçe", "Toplam Harcama"};
        Object[][] customerData = new Object[customerList.size()][4];

        // Fill the data array with customerList content
        for (int i = 0; i < customerList.size(); i++) {
            Customer customer = customerList.get(i);
            customerData[i][0] = customer.getCustomerName();
            customerData[i][1] = customer.getCustomerType();
            customerData[i][2] = customer.getBudget();
            customerData[i][3] = customer.getTotalSpent();
        }

        // Recreate the table model with updated data
        DefaultTableModel customerTableModel = new DefaultTableModel(customerData, customerColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        // Update the table with the new model
        customerTable.setModel(customerTableModel);
        customerTable.revalidate();  // Revalidate the table to reflect changes
        customerTable.repaint();  // Repaint to ensure UI refresh
    }

    public static ArrayList<Product> getProductList() {
        return productList;
    }

    public static ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    public static ArrayList<Order> getOrderList() {
        return orderList;
    }

    public static void setProductList(ArrayList<Product> productList) {
        MainPage.productList = productList;
    }

    public static void setCustomerList(ArrayList<Customer> customerList) {
        MainPage.customerList = customerList;
    }

    public static void setOrderList(ArrayList<Order> orderList) {
        MainPage.orderList = orderList;
    }

    public static ArrayList<Log> getLogList() {
        return logList;
    }

    public static void setLogList(ArrayList<Log> logList) {
        MainPage.logList = logList;
    }

    public static JButton getOrderButton() {
        return orderButton;
    }

}
