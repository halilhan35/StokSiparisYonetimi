package org.example.pages;

import org.example.classes.Log;
import org.example.database.ConnectDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class LogPage {

    private Connection connection;
    private static ArrayList<Log> logList = new ArrayList<>();

    private JFrame frame = new JFrame("Products Orders App");

    private static JTable logTable;

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

        logList = MainPage.getLogList();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

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

        panel.setLayout(null);
        frame.setContentPane(panel);

        // LOG KAYITLARI başlığı
        JLabel logTitle = new JLabel("LOG KAYITLARI", SwingConstants.CENTER);
        logTitle.setFont(new Font("Inter", Font.PLAIN, 60));
        logTitle.setForeground(Color.WHITE);
        logTitle.setBounds(170, 30, 860, 80);
        panel.add(logTitle);

        // log kayıtları için rectangle
        JPanel logRectangle = new JPanel();
        logRectangle.setBackground(Color.decode("#767676"));
        logRectangle.setBounds(50, 130, 1100, 630);
        logRectangle.setLayout(new BorderLayout()); // BorderLayout kullanılmalı
        panel.add(logRectangle);

        String[] logColumnNames = {"Log ID", "Müşteri ID", "Sipariş ID", "Log Tarihi", "Log Tipi", "Log Detayı"};
        Object[][] logData = new Object[logList.size()][6];

        // Veriler dolduruluyor
        for (int i = 0; i < logList.size(); i++) {
            Log log = logList.get(i);
            logData[i][0] = log.getLogID();
            logData[i][1] = log.getCustomerID();
            logData[i][2] = log.getOrderID();
            logData[i][3] = log.getLogDate();
            logData[i][4] = log.getLogType();
            logData[i][5] = log.getLogDetails();
        }

        DefaultTableModel logTableModel = new DefaultTableModel(logData, logColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(logTableModel);
        logTable.setFont(new Font("Inter", Font.PLAIN, 16));
        logTable.setRowHeight(30);
        logTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 16));
        logTable.getTableHeader().setBackground(new Color(0x555555));
        logTable.getTableHeader().setForeground(Color.WHITE);
        logTable.setBackground(new Color(0xE0E0E0));
        logTable.setForeground(Color.BLACK);

        // Yatay kaydırma çubuğu için AUTO_RESIZE_OFF modunu ayarla sadece ilk defa
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Sütun genişliklerini ayarla (ilk defa tabloyu oluşturduktan sonra)
        setTableColumnWidths();

        JScrollPane scrollPaneLog = new JScrollPane(logTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneLog.setBounds(0, 0, logRectangle.getWidth(), logRectangle.getHeight());
        scrollPaneLog.setBackground(new Color(0x767676));
        scrollPaneLog.setBorder(BorderFactory.createEmptyBorder());
        logRectangle.add(scrollPaneLog);

        // frame boyutları ve görünürlük
        frame.setSize(1200, 840);
        frame.setVisible(true);
    }

    // Tabloyu güncellerken sütun genişliklerini yeniden ayarla
    public static void updateLogsTable() {
        String[] logColumnNames = {"Log ID", "Müşteri ID", "Sipariş ID", "Log Tarihi", "Log Tipi", "Log Detayı"};
        Object[][] logData = new Object[logList.size()][6];

        // Veriler dolduruluyor
        for (int i = 0; i < logList.size(); i++) {
            Log log = logList.get(i);
            logData[i][0] = log.getLogID();
            logData[i][1] = log.getCustomerID();
            logData[i][2] = log.getOrderID();
            logData[i][3] = log.getLogDate();
            logData[i][4] = log.getLogType();
            logData[i][5] = log.getLogDetails();
        }

        DefaultTableModel logTableModel = new DefaultTableModel(logData, logColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable.setModel(logTableModel);

        // Sütun genişliklerini yeniden ayarla
        setTableColumnWidths();

        logTable.revalidate();  // Revalidate the table to reflect changes
        logTable.repaint();  // Repaint to ensure UI refresh
    }

    // Tablo sütun genişliklerini ayarlamak için yardımcı fonksiyon
    private static void setTableColumnWidths() {
        logTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Log ID
        logTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Müşteri ID
        logTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Sipariş ID
        logTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Log Tarihi
        logTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Log Tipi
        logTable.getColumnModel().getColumn(5).setPreferredWidth(800); // Log Detayı (daha geniş)
    }

    public static JTable getLogTable() {
        return logTable;
    }

}
