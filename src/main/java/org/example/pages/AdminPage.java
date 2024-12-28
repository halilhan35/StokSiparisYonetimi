package org.example.pages;
import org.example.Application;
import org.example.classes.Customer;
import org.example.classes.Order;
import org.example.classes.Product;
import org.example.database.ConnectDB;
import org.example.progressbar.StockProgressBarRenderer;
import org.example.threads.ControllerThread;
import org.example.threads.OrderThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AdminPage {

    private Connection connection;

    private static ArrayList<Product> productList = new ArrayList<Product>();
    private static ArrayList<Order> orderList = new ArrayList<Order>();
    private static ArrayList<Customer> customerList = new ArrayList<Customer>();

    private static JFrame frame = new JFrame("Products Orders App");

    private JTextField productNameField = new JTextField();
    private JTextField productStockField = new JTextField();
    private JTextField productPriceField = new JTextField();
    private JPanel productListPanel = new JPanel(null);
    private JTable productTable;
    private JTextField deleteProductNameField = new JTextField();
    private JTextField updateProductNameField = new JTextField();
    private JTextField updateStockField = new JTextField();

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

        productList = Application.getProductList();
        orderList = Application.getOrderList();
        customerList = Application.getCustomerList();

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
        panel.setBounds(59, 72, 1200, 840);
        panel.setBackground(Color.decode("#1E1E1E"));
        frame.setContentPane(panel);

        JButton returnButton = new JButton("Geri");
        returnButton.setBounds(30, 10, 80, 40);
        returnButton.setBackground(Color.decode("#D9D9D9"));
        returnButton.setForeground(Color.BLACK);
        returnButton.setFont(new Font("Inter", Font.PLAIN, 20));
        panel.add(returnButton);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Application.getMainPage().getFrame().setVisible(true);

            }
        });

        // Product addition panel
        JPanel productAdditionPanel = new JPanel(null);
        productAdditionPanel.setBounds(30, 59, 320, 520);
        productAdditionPanel.setBackground(Color.decode("#767676"));
        panel.add(productAdditionPanel);

        JLabel productAdditionLabel = new JLabel("Ürün Ekleme");
        productAdditionLabel.setBounds(65, 55, 250, 46);
        productAdditionLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productAdditionLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productAdditionLabel);

        JLabel productNameLabel = new JLabel("Ürün Adı :");
        productNameLabel.setBounds(20, 129, 125, 30);
        productNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productNameLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productNameLabel);

        productNameField.setBounds(20, 177, 270, 40);
        productNameField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productNameField);

        JLabel productStockLabel = new JLabel("Ürün Stoğu :");
        productStockLabel.setBounds(20, 236, 150, 30);
        productStockLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productStockLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productStockLabel);

        productStockField.setBounds(20, 285, 270, 40);
        productStockField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productStockField);

        JLabel productPriceLabel = new JLabel("Ürün Fiyatı :");
        productPriceLabel.setBounds(20, 344, 150, 30);
        productPriceLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productPriceLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productPriceLabel);

        productPriceField.setBounds(20, 393, 270, 40);
        productPriceField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productPriceField);

        JButton addProductButton = new JButton("Ürünü Ekle");
        addProductButton.setEnabled(false);
        addProductButton.setBounds(50, 463, 200, 40);
        addProductButton.setBackground(Color.decode("#D9D9D9"));
        addProductButton.setForeground(Color.BLACK);
        addProductButton.setFont(new Font("Inter", Font.PLAIN, 24));
        addProductButton.setBorder(BorderFactory.createEmptyBorder());
        addProductButton.setFocusPainted(false);
        addProductButton.setOpaque(true);
        addProductButton.setContentAreaFilled(true);
        addProductButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        productAdditionPanel.add(addProductButton);

        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        // Product list panel
        JLabel productListLabel = new JLabel("Ürün Listesi");
        productListLabel.setBounds(50, 595, 311, 44);
        productListLabel.setFont(new Font("Inter", Font.PLAIN, 40));
        productListLabel.setForeground(Color.WHITE);
        panel.add(productListLabel);

        productListPanel.setBounds(50, 655, 1100, 120);
        productListPanel.setBackground(Color.decode("#767676"));
        panel.add(productListPanel);

        String[] productColumnNames = {"Ürün ID", "Ürün Adı", "Stok", "Fiyat", "Stok Durumu"};
        Object[][] productData = new Object[productList.size()][5];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductID();
            productData[i][1] = product.getProductName();
            productData[i][2] = product.getStock();
            productData[i][3] = product.getPrice();

            // Stok yüzdesini hesapla (maksimum stok 500 olarak varsayılmış)
            int stockPercentage = (int) ((product.getStock() / 500.0) * 100);
            productData[i][4] = stockPercentage;
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

        productTable.getColumnModel().getColumn(4).setCellRenderer(new StockProgressBarRenderer());

        JScrollPane scrollPaneProduct = new JScrollPane(productTable);
        scrollPaneProduct.setBounds(0, 0, productListPanel.getWidth(), productListPanel.getHeight());
        scrollPaneProduct.setBackground(new Color(0x767676));
        scrollPaneProduct.setBorder(BorderFactory.createEmptyBorder());

        productListPanel.setLayout(null);
        productListPanel.add(scrollPaneProduct);

        // Product deletion panel
        JPanel productDeletionPanel = new JPanel(null);
        productDeletionPanel.setBounds(415, 39, 320, 220);
        productDeletionPanel.setBackground(Color.decode("#767676"));
        panel.add(productDeletionPanel);

        JLabel productDeletionLabel = new JLabel("Ürün Silme");
        productDeletionLabel.setBounds(80, 20, 180, 30);
        productDeletionLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productDeletionLabel.setForeground(Color.WHITE);
        productDeletionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        productDeletionPanel.add(productDeletionLabel);

        JLabel deleteProductNameLabel = new JLabel("Silinecek Ürün Adı :");
        deleteProductNameLabel.setBounds(25, 80, 245, 30);
        deleteProductNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        deleteProductNameLabel.setForeground(Color.WHITE);
        productDeletionPanel.add(deleteProductNameLabel);

        deleteProductNameField.setBounds(25, 120, 270, 40);
        deleteProductNameField.setBackground(Color.decode("#D9D9D9"));
        productDeletionPanel.add(deleteProductNameField);

        JButton deleteProductButton = new JButton("Ürünü Sil");
        deleteProductButton.setEnabled(false);
        deleteProductButton.setBounds(60, 170, 200, 40);
        deleteProductButton.setBackground(Color.decode("#D9D9D9"));
        deleteProductButton.setForeground(Color.BLACK);
        deleteProductButton.setFont(new Font("Inter", Font.PLAIN, 24));
        deleteProductButton.setBorder(BorderFactory.createEmptyBorder());
        deleteProductButton.setOpaque(true);
        deleteProductButton.setContentAreaFilled(true);
        deleteProductButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        productDeletionPanel.add(deleteProductButton);

        deleteProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });

        // Product update panel
        JPanel productUpdatePanel = new JPanel(null);
        productUpdatePanel.setBounds(415, 280, 320, 320);
        productUpdatePanel.setBackground(Color.decode("#767676"));
        panel.add(productUpdatePanel);

        JLabel productUpdateLabel = new JLabel("Ürün Güncelleme");
        productUpdateLabel.setBounds(25, 20, 280, 30);
        productUpdateLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productUpdateLabel.setForeground(Color.WHITE);
        productUpdateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        productUpdatePanel.add(productUpdateLabel);

        JLabel updateProductNameLabel = new JLabel("Güncellenecek Ürün Adı :");
        updateProductNameLabel.setBounds(15, 80, 300, 30);
        updateProductNameLabel.setFont(new Font("Inter", Font.PLAIN, 20));
        updateProductNameLabel.setForeground(Color.WHITE);
        productUpdatePanel.add(updateProductNameLabel);

        updateProductNameField.setBounds(20, 120, 280, 40);
        updateProductNameField.setBackground(Color.decode("#D9D9D9"));
        productUpdatePanel.add(updateProductNameField);

        JLabel updateStockLabel = new JLabel("Güncellenecek Stok Miktarı :");
        updateStockLabel.setBounds(15, 180, 300, 30);
        updateStockLabel.setFont(new Font("Inter", Font.PLAIN, 20));
        updateStockLabel.setForeground(Color.WHITE);
        productUpdatePanel.add(updateStockLabel);

        updateStockField.setBounds(20, 220, 280, 40);
        updateStockField.setBackground(Color.decode("#D9D9D9"));
        productUpdatePanel.add(updateStockField);

        JButton updateProductButton = new JButton("Ürünü Güncelle");
        updateProductButton.setEnabled(false);
        updateProductButton.setBounds(60, 270, 200, 40);
        updateProductButton.setBackground(Color.decode("#D9D9D9"));
        updateProductButton.setForeground(Color.BLACK);
        updateProductButton.setFont(new Font("Inter", Font.PLAIN, 24));
        updateProductButton.setBorder(BorderFactory.createEmptyBorder());
        updateProductButton.setOpaque(true);
        updateProductButton.setContentAreaFilled(true);
        updateProductButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        productUpdatePanel.add(updateProductButton);

        updateProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });

        // Approval Pending Label
        JLabel approvalPendingLabel = new JLabel("Onay Bekleyenler");
        approvalPendingLabel.setBounds(770, 102, 380, 45);
        approvalPendingLabel.setFont(new Font("Inter", Font.PLAIN, 40));
        approvalPendingLabel.setForeground(Color.WHITE);
        approvalPendingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(approvalPendingLabel);

        // System Hold Button
        JButton holdSystemButton = new JButton("Sistemi Beklemeye Al");
        holdSystemButton.setBounds(788, 39, 344, 40);
        holdSystemButton.setBackground(Color.GREEN);
        holdSystemButton.setForeground(Color.WHITE);
        holdSystemButton.setFont(new Font("Inter", Font.PLAIN, 24));
        holdSystemButton.setBorder(BorderFactory.createEmptyBorder());
        holdSystemButton.setOpaque(true);
        holdSystemButton.setContentAreaFilled(true);
        holdSystemButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        holdSystemButton.setBorder(BorderFactory.createEmptyBorder());
        panel.add(holdSystemButton);

        holdSystemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Eğer butonun rengi yeşilse
                if (holdSystemButton.getBackground() == Color.GREEN) {
                    // Beklemeye al işlemi
                    Application.getMainPage().getOrderButton().setEnabled(false);
                    OrderThread.stopThread();
                    addProductButton.setEnabled(true);
                    deleteProductButton.setEnabled(true);
                    updateProductButton.setEnabled(true);
                    holdSystemButton.setBackground(Color.RED);
                    holdSystemButton.setText("Sistemi Devam Ettir");
                }
                else if (holdSystemButton.getBackground() == Color.RED) {
                    Application.getMainPage().getOrderButton().setEnabled(true);
                    OrderThread.resetStopFlag();
                    addProductButton.setEnabled(false);
                    deleteProductButton.setEnabled(false);
                    updateProductButton.setEnabled(false);
                    holdSystemButton.setBackground(Color.GREEN);
                    holdSystemButton.setText("Sistemi Beklemeye Al");
                }
            }
        });

        // Approval Pending Table Rectangle
        JPanel approvalPendingPanel = new JPanel(null);
        approvalPendingPanel.setBounds(770, 171, 400, 260);
        approvalPendingPanel.setBackground(Color.decode("#767676"));
        panel.add(approvalPendingPanel);

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
        scrollPaneOrder.setBounds(0, 0, approvalPendingPanel.getWidth(), approvalPendingPanel.getHeight());
        scrollPaneOrder.setBackground(new Color(0x767676));
        scrollPaneOrder.setBorder(BorderFactory.createEmptyBorder());

        // Add the scroll pane to the rectangle
        approvalPendingPanel.setLayout(null);
        approvalPendingPanel.add(scrollPaneOrder);

        // Approval Button
        JButton approveButton = new JButton("Onay Ver");
        approveButton.setBounds(855, 491, 230, 40);
        approveButton.setBackground(Color.decode("#D9D9D9"));
        approveButton.setForeground(Color.BLACK);
        approveButton.setFont(new Font("Inter", Font.PLAIN, 28));
        approveButton.setBorder(BorderFactory.createEmptyBorder());
        approveButton.setOpaque(true);
        approveButton.setContentAreaFilled(true);
        approveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(approveButton);

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = now.format(formatter);

                for (Order order : orderList) {
                    double priority = 0.0;
                    Customer customer = null;

                    // Müşteriyi bul
                    for (Customer customer1 : customerList) {
                        if (order.getCustomerID() == customer1.getCustomerID()) {
                            customer = customer1;
                        }
                    }

                    if (customer != null) {
                        // Müşteri türüne göre öncelik artırımı
                        if (customer.getCustomerType().equals("Premium")) {
                            priority += 15;
                        } else {
                            priority += 10;
                        }

                        // OrderDate'i dönüştür ve saniye farkını hesapla
                        String orderDate = order.getOrderDate();
                        try {
                            LocalDateTime orderDateTime = LocalDateTime.parse(orderDate, formatter);
                            LocalDateTime currentDateTime = LocalDateTime.parse(formattedDate, formatter);

                            long secondsDifference = java.time.Duration.between(orderDateTime, currentDateTime).getSeconds();
                            priority += secondsDifference * 0.5;

                            order.setPriority(priority);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                // Onceligi en yuksek olana gore sıralama

                Collections.sort(orderList, new Comparator<Order>() {
                    @Override
                    public int compare(Order o1, Order o2) {
                        // Azalan sırayla sıralamak için tersini alıyoruz
                        return Double.compare(o2.getPriority(), o1.getPriority());
                    }
                });

                for (Order order1 : orderList) {
                    System.out.println("Order ID: " + order1.getOrderID() + ", Priority: " + order1.getPriority());
                }

                // Burada her sipariş için thread olmalı.
                ControllerThread controllerThread = new ControllerThread(orderList, connection);
                controllerThread.start();

                try {
                    controllerThread.join();
                } catch (InterruptedException m) {
                    m.printStackTrace();
                }

                orderList.clear();
                Application.setOrderList(orderList);
                Application.getMainPage().updateOrdersTable();
                Application.getAdminPage().setOrderList(orderList);
                Application.getAdminPage().updateOrdersTable();
                Application.getProductList().clear();
                Application.selectAllProducts();
                Application.getMainPage().updateProductTable();
                Application.getAdminPage().updateProductTable();

            }
        });

        frame.setSize(1200, 840);
        frame.setVisible(true);
    }

    public void addProduct(){
        String query = "INSERT INTO products (productname, stock, price) VALUES (?, ?, ?)";

        String productName = productNameField.getText();
        int stock;
        String stockText = productStockField.getText();
        double price;
        String priceText = productPriceField.getText();

        for(Product product : productList){
            if(product.getProductName().equals(productName)){
                JOptionPane.showMessageDialog(null, "Eklemeye çalıştığınız ürün veritabanında zaten bulunuyor.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) {
                JOptionPane.showMessageDialog(null, "Stok miktarı negatif olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Stok için lütfen geçerli bir sayı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(null, "Ürün fiyatı negatif olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Ürün fiyatı için geçerli bir sayı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, productName);
            stmt.setInt(2, stock);
            stmt.setDouble(3, price);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // ID'yi al

                    Product newProduct = new Product(generatedId, productName, stock, price);
                    productList.add(newProduct);

                    Application.setProductList(productList);

                    updateProductTable();
                    Application.getMainPage().updateProductTable();

                    JOptionPane.showMessageDialog(null, "Ürün başarıyla eklendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ürün veritabanına kaydedilirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void deleteProduct() {

        String productName = deleteProductNameField.getText();
        boolean control = false;

        // Check if the product exists in the list
        for(Product product : productList){
            if(product.getProductName().equals(productName)){
                control = true;
            }
        }

        if(!control){
            JOptionPane.showMessageDialog(null, "Silmeye çalıştığınız ürün veritabanında bulunmuyor.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "DELETE FROM products WHERE productname = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, productName);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Remove the product from the list
                productList.removeIf(product -> product.getProductName().equals(productName));
                Application.setProductList(productList);

                // Update the table
                updateProductTable();
                Application.getMainPage().updateProductTable();

                JOptionPane.showMessageDialog(null, "Ürün başarıyla silindi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Ürün silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Ürün veritabanından silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProduct(){

        String productName = updateProductNameField.getText();
        boolean control = false;
        int stock;
        String stockText = updateStockField.getText();

        // Check if the product exists in the list
        for(Product product : productList){
            if(product.getProductName().equals(productName)){
                control = true;
            }
        }

        if(!control){
            JOptionPane.showMessageDialog(null, "Güncellemeye çalıştığınız ürün veritabanında bulunmuyor.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) {
                JOptionPane.showMessageDialog(null, "Stok miktarı negatif olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Stok için lütfen geçerli bir sayı girin.", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String updateQuery = "UPDATE products SET stock = ? WHERE productname = ?";

            PreparedStatement stmt = connection.prepareStatement(updateQuery);

            stmt.setInt(1, stock);
            stmt.setString(2, productName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Ürün stoğu başarıyla güncellendi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                for(Product product : productList){
                    if(product.getProductName().equals(productName)){
                       product.setStock(stock);
                    }
                }

                Application.setProductList(productList);
                Application.getMainPage().updateProductTable();
                updateProductTable();

            } else {
                JOptionPane.showMessageDialog(null, "Ürün stoğu güncellenemedi. Lütfen malzeme adını kontrol edin.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void updateProductTable() {
        String[] productColumnNames = {"Ürün ID", "Ürün Adı", "Stok", "Fiyat", "Stok Durumu"};
        Object[][] productData = new Object[productList.size()][5];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductID();
            productData[i][1] = product.getProductName();
            productData[i][2] = product.getStock();
            productData[i][3] = product.getPrice();

            // Stok yüzdesi hesaplama (maksimum stok 500 varsayıldı)
            int stockPercentage = Math.min((int) ((product.getStock() / 500.0) * 100), 100);
            productData[i][4] = stockPercentage;
        }

        DefaultTableModel productTableModel = new DefaultTableModel(productData, productColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Yeni modeli tabloya set et
        productTable.setModel(productTableModel);

        // Stok Durumu sütunu için renderer ekle
        productTable.getColumnModel().getColumn(4).setCellRenderer(new StockProgressBarRenderer());

        // UI güncellemeleri
        productTable.revalidate(); // Tabloyu yeniden doğrula
        productTable.repaint(); // UI'yi yeniden çiz
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

    public static ArrayList<Product> getProductList() {
        return productList;
    }

    public static void setProductList(ArrayList<Product> productList) {
        AdminPage.productList = productList;
    }

    public static void setOrderList(ArrayList<Order> orderList) {
        AdminPage.orderList = orderList;
    }

    public static JTable getOrderTable() {
        return orderTable;
    }

    public static JFrame getFrame() {
        return frame;
    }
}
