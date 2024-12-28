package org.example.pages;

import org.example.Application;
import org.example.classes.Customer;
import org.example.classes.Order;
import org.example.classes.Product;
import org.example.database.ConnectDB;
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
import java.util.ArrayList;

public class MainPage {

    private ArrayList<Order> preCustomers = new ArrayList<Order>();
    private ArrayList<Order> staCustomers = new ArrayList<Order>();

    private static JButton orderButton = new JButton("Sipariş Ver");

    private Connection connection;

    private static JFrame frame = new JFrame("Products Orders App");
    private static JTable productTable;
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

        for (Order order1 : Application.getOrderList()) {
            for (Customer customer : Application.getCustomerList()) {
                if(order1.getCustomerID() == customer.getCustomerID()) {
                    if (customer.getCustomerType().equals("Premium")) {
                        preCustomers.add(order1);
                    } else if (customer.getCustomerType().equals("Standard")) {
                        staCustomers.add(order1);
                    }
                }
            }
        }

        Application.getOrderList().clear();

        Application.getOrderList().addAll(preCustomers);
        Application.getOrderList().addAll(staCustomers);

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

        JButton returnButton = new JButton("Geri");
        returnButton.setBounds(30, 40, 80, 40);
        returnButton.setBackground(Color.decode("#D9D9D9"));
        returnButton.setForeground(Color.BLACK);
        returnButton.setFont(new Font("Inter", Font.PLAIN, 20));
        panel.add(returnButton);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frame.dispose();
                Application.getLoginPage().getFrame().setVisible(true);

            }
        });

        JLabel productLabel = new JLabel("SİPARİŞ");
        productLabel.setBounds(120, 120, 150, 40);
        productLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        productLabel.setForeground(Color.WHITE);
        panel.add(productLabel);

        // Product Name Label
        JLabel productNameLabel = new JLabel("Ürün Adı:");
        productNameLabel.setBounds(50, 180, 240, 40);
        productNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productNameLabel.setForeground(Color.WHITE);
        panel.add(productNameLabel);

        // Product Number Label
        JLabel productNumberLabel = new JLabel("Ürün Miktarı:");
        productNumberLabel.setBounds(50, 290, 240, 40);
        productNumberLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        productNumberLabel.setForeground(Color.WHITE);
        panel.add(productNumberLabel);

        // Product Name TextField
        JTextField productNameField = new JTextField();
        productNameField.setBounds(50, 230, 250, 40);
        productNameField.setBackground(new Color(0xE0E0E0));
        productNameField.setFont(new Font("Inter", Font.PLAIN, 20));
        productNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(productNameField);

        // Product Number TextField
        JTextField productNumberField = new JTextField();
        productNumberField.setBounds(50, 340, 250, 40);
        productNumberField.setBackground(new Color(0xE0E0E0));
        productNumberField.setFont(new Font("Inter", Font.PLAIN, 20));
        productNumberField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(productNumberField);

        // Order Button
        orderButton.setEnabled(true);
        orderButton.setBounds(53, 420, 240, 40);
        orderButton.setBackground(new Color(0xE0E0E0));
        orderButton.setForeground(Color.BLACK);
        orderButton.setFont(new Font("Inter", Font.PLAIN, 24));
        orderButton.setOpaque(true);
        orderButton.setContentAreaFilled(true);
        orderButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.add(orderButton);

        orderButton.addActionListener(e -> {
            if(Application.getLoginPage().getLoginType().equals("Customer")) {
                String customerName = LoginPage.getCustomer().getCustomerName();
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

                if(quantity>5){
                    JOptionPane.showMessageDialog(frame, "Bir üründen 5'ten fazla alamazsınız!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                OrderThread orderThread = new OrderThread(customerName, productName, quantity, connection);
                orderThread.start();
            }
            else {
                JOptionPane.showMessageDialog(frame, "Yalnızca müşteriler sipariş verebilir!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Product Table Panel
        JPanel productTablePanel = new JPanel();
        productTablePanel.setBounds(359, 105, 320, 360);
        productTablePanel.setBackground(new Color(0x767676));
        panel.add(productTablePanel);

        String[] productColumnNames = {"Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[Application.getProductList().size()][3];

        for (int i = 0; i < Application.getProductList().size(); i++) {
            Product product = Application.getProductList().get(i);
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
        Object[][] orderData = new Object[Application.getOrderList().size()][3];

        for (int i = 0; i < Application.getOrderList().size(); i++) {
            Order order = Application.getOrderList().get(i);
            Customer customer = null;

            for(int j = 0 ; j < Application.getCustomerList().size(); j++){
                if(order.getCustomerID() == Application.getCustomerList().get(j).getCustomerID()){
                    customer = Application.getCustomerList().get(j);
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
                if(Application.getLoginPage().getLoginType().equals("Admin")) {
                    frame.dispose();
                    Application.getAdminPage().getFrame().setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Bu sayfaya erişiminiz yok!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
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
                if(Application.getLoginPage().getLoginType().equals("Admin")) {
                    frame.dispose();
                    Application.getLogPage().getFrame().setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Bu sayfaya erişiminiz yok!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Frame properties
        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

    public void updateProductTable() {
        String[] productColumnNames = {"Ürün Adı", "Stok", "Fiyat"};
        Object[][] productData = new Object[Application.getProductList().size()][3];

        for (int i = 0; i < Application.getProductList().size(); i++) {
            Product product = Application.getProductList().get(i);
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

    public void updateOrdersTable() {
        String[] orderColumnNames = {"Müşteri Türü", "Müşteri Adı", "Harcama"};
        Object[][] orderData = new Object[Application.getOrderList().size()][3];

        for (int i = 0; i < Application.getOrderList().size(); i++) {
            Order order = Application.getOrderList().get(i);
            Customer customer = null;

            for(int j = 0 ; j < Application.getCustomerList().size(); j++){
                if(order.getCustomerID() == Application.getCustomerList().get(j).getCustomerID()){
                    customer = Application.getCustomerList().get(j);
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

    public JButton getOrderButton() {
        return orderButton;
    }

    public static JFrame getFrame() {
        return frame;
    }
}
