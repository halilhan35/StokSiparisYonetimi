package org.example.pages;
import org.example.classes.Product;
import org.example.database.ConnectDB;

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
import java.util.ArrayList;

public class AdminPage {

    private Connection connection;

    private static ArrayList<Product> productList = new ArrayList<Product>();

    private JFrame frame = new JFrame("Products Orders App");

    private JTextField productNameField = new JTextField();
    private JTextField productStockField = new JTextField();
    private JTextField productPriceField = new JTextField();
    private JPanel productListPanel = new JPanel(null);
    private JTable productTable;
    private JTextField deleteProductNameField = new JTextField();
    private JTextField updateProductNameField = new JTextField();
    private JTextField updateStockField = new JTextField();


    public void createAndShowGUI() {

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection = ConnectDB.getConnection();

        productList = MainPage.getProductList();

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

        // Product addition panel
        JPanel productAdditionPanel = new JPanel(null);
        productAdditionPanel.setBounds(20, 39, 320, 520);
        productAdditionPanel.setBackground(Color.decode("#767676"));
        panel.add(productAdditionPanel);

        JLabel productAdditionLabel = new JLabel("Ürün Ekleme");
        productAdditionLabel.setBounds(65, 50, 250, 46);
        productAdditionLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productAdditionLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productAdditionLabel);

        JLabel productNameLabel = new JLabel("Ürün Adı :");
        productNameLabel.setBounds(20, 124, 125, 30);
        productNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productNameLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productNameLabel);

        productNameField.setBounds(20, 172, 270, 40);
        productNameField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productNameField);

        JLabel productStockLabel = new JLabel("Ürün Stoğu :");
        productStockLabel.setBounds(20, 231, 150, 30);
        productStockLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productStockLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productStockLabel);

        productStockField.setBounds(20, 280, 270, 40);
        productStockField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productStockField);

        JLabel productPriceLabel = new JLabel("Ürün Fiyatı :");
        productPriceLabel.setBounds(20, 339, 150, 30);
        productPriceLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productPriceLabel.setForeground(Color.WHITE);
        productAdditionPanel.add(productPriceLabel);

        productPriceField.setBounds(20, 388, 270, 40);
        productPriceField.setBackground(Color.decode("#D9D9D9"));
        productAdditionPanel.add(productPriceField);

        JButton addProductButton = new JButton("Ürünü Ekle");
        addProductButton.setBounds(50, 458, 200, 40);
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

        String[] productColumnNames = {"Ürün ID","Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[productList.size()][4];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductID();
            productData[i][1] = product.getProductName();
            productData[i][2] = product.getStock();
            productData[i][3] = product.getPrice();
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
        scrollPaneProduct.setBounds(0, 0, productListPanel.getWidth(), productListPanel.getHeight());
        scrollPaneProduct.setBackground(new Color(0x767676));
        scrollPaneProduct.setBorder(BorderFactory.createEmptyBorder());

        // Add the scroll pane to the rectangle
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
        holdSystemButton.setBackground(Color.decode("#00FF11"));
        holdSystemButton.setForeground(Color.WHITE);
        holdSystemButton.setFont(new Font("Inter", Font.PLAIN, 24));
        holdSystemButton.setBorder(BorderFactory.createEmptyBorder());
        holdSystemButton.setOpaque(true);
        holdSystemButton.setContentAreaFilled(true);
        holdSystemButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        holdSystemButton.setBorder(BorderFactory.createEmptyBorder());
        panel.add(holdSystemButton);

        // Approval Pending Table Rectangle
        JPanel approvalPendingPanel = new JPanel(null);
        approvalPendingPanel.setBounds(770, 171, 400, 260);
        approvalPendingPanel.setBackground(Color.decode("#767676"));
        panel.add(approvalPendingPanel);

        // Approval Customer Name Label
        JLabel approvalCustomerNameLabel = new JLabel("Onay Verilecek Müşteri Adı :");
        approvalCustomerNameLabel.setBounds(805, 468, 402, 30);
        approvalCustomerNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        approvalCustomerNameLabel.setForeground(Color.WHITE);
        panel.add(approvalCustomerNameLabel);

        // Approval Customer Name Field
        JTextField approvalCustomerNameField = new JTextField();
        approvalCustomerNameField.setBounds(795, 527, 350, 40);
        approvalCustomerNameField.setBackground(Color.decode("#D9D9D9"));
        panel.add(approvalCustomerNameField);

        // Approval Button
        JButton approveButton = new JButton("Onay Ver");
        approveButton.setBounds(855, 591, 230, 40);
        approveButton.setBackground(Color.decode("#D9D9D9"));
        approveButton.setForeground(Color.BLACK);
        approveButton.setFont(new Font("Inter", Font.PLAIN, 28));
        approveButton.setBorder(BorderFactory.createEmptyBorder());
        approveButton.setOpaque(true);
        approveButton.setContentAreaFilled(true);
        approveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(approveButton);

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

                    MainPage.setProductList(productList);

                    updateProductTable();
                    MainPage.updateProductTable();

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
                MainPage.setProductList(productList);

                // Update the table
                updateProductTable();
                MainPage.updateProductTable();

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

                MainPage.setProductList(productList);
                MainPage.updateProductTable();
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
        String[] productColumnNames = {"Ürün ID", "Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[productList.size()][4];

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            productData[i][0] = product.getProductID();
            productData[i][1] = product.getProductName();
            productData[i][2] = product.getStock();
            productData[i][3] = product.getPrice();
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

    public static ArrayList<Product> getProductList() {
        return productList;
    }

    public static void setProductList(ArrayList<Product> productList) {
        AdminPage.productList = productList;
    }

}
