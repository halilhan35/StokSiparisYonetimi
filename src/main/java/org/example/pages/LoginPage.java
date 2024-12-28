package org.example.pages;

import org.example.Application;
import org.example.classes.Admin;
import org.example.classes.Customer;
import org.example.database.ConnectDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginPage {

    private static Connection connection;
    private static Customer customer;
    private static JFrame frame = new JFrame("Products Orders App");
    private static ArrayList<Customer> customerList = new ArrayList<Customer>();
    private static ArrayList<Admin> adminList = new ArrayList<Admin>();

    private static String loginType = "";

    public void createAndShowGUI() {

        // Connection kontrolü ve kapatma
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        connection = ConnectDB.getConnection();

        // Arka plan resmi için özel JPanel
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

        selectAllCustomers();
        selectAllAdmins();

        panel.setLayout(null);
        frame.setContentPane(panel);

        // "GİRİŞ" Başlığı
        JLabel titleLabel = new JLabel("GİRİŞ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(75, 21, 250, 39);
        panel.add(titleLabel);

        // "Müşteri Adı :" Metni
        JLabel customerNameLabel = new JLabel("Müşteri/Admin Adı :");
        customerNameLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        customerNameLabel.setForeground(Color.WHITE);
        customerNameLabel.setBounds(80, 107, 260, 29);
        panel.add(customerNameLabel);

        // Müşteri Adı Text Field
        JTextField customerNameField = new JTextField();
        customerNameField.setBounds(80, 162, 240, 29);
        customerNameField.setBackground(new Color(217, 217, 217));
        panel.add(customerNameField);

        // "Şifre :" Metni
        JLabel passwordLabel = new JLabel("Şifre :");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 24));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(80, 217, 240, 29);
        panel.add(passwordLabel);

        // Şifre Text Field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(80, 272, 240, 29);
        passwordField.setBackground(new Color(217, 217, 217));
        panel.add(passwordField);

        // Giriş Yap Butonu
        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(80, 380, 240, 40);
        loginButton.setBackground(Color.decode("#D9D9D9"));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Inter", Font.PLAIN, 20));
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String entryCustomerName = customerNameField.getText();
                String entryCustomerPassword = passwordField.getText();

                if(entryCustomerName.equals("") || entryCustomerPassword.equals("")){
                    JOptionPane.showMessageDialog(frame, "Tüm bilgilerini doldurun!", "Eksik Bilgi", JOptionPane.ERROR_MESSAGE);
                }

                for(Customer customer1 : customerList){
                    if(customer1.getCustomerName().equals(entryCustomerName)){
                        if(customer1.getCustomerPassword().equals(entryCustomerPassword)){
                            customer = customer1;

                            loginType = "Customer";

                            frame.dispose();
                            Application.getMainPage().getFrame().setVisible(true);
                        }
                        else {
                            JOptionPane.showMessageDialog(frame, "Girdğiniz şifre yanlış!", "Eksik Bilgi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                for(Admin admin : adminList){
                    if(admin.getAdminName().equals(entryCustomerName)){
                        if(admin.getAdminPassword().equals(entryCustomerPassword)){
                            loginType = "Admin";

                            frame.dispose();
                            Application.getMainPage().getFrame().setVisible(true);
                        }
                        else {
                            JOptionPane.showMessageDialog(frame, "Girdğiniz şifre yanlış!", "Eksik Bilgi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

            }
        });

        frame.setSize(400, 470);
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
                String customerPassword = rs.getString("password");

                Customer customer = new Customer(customerID,customerName,customerPassword,budget,customerType,totalSpent);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectAllAdmins() {
        try {
            String query = "SELECT * FROM admins";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int adminID = rs.getInt("adminid");
                String adminName = rs.getString("adminname");
                String adminPassword = rs.getString("password");

                Admin admin = new Admin(adminID,adminName,adminPassword);
                adminList.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomer() {
        return customer;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public static String getLoginType() {
        return loginType;
    }
}
