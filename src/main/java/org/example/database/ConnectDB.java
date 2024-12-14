package org.example.database;

import java.sql.*;

public class ConnectDB {

    private static Connection connection = null;

    private ConnectDB() {
        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://localhost:5432/siparisstokdb";
            String user = "postgres";
            String password = "2004";

            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                System.out.println("Veri Tabanı bağlantısı başarılı");
            } else {
                System.out.println("Veri Tabanı bağlanma hatası");
            }
        } catch (SQLException e) {
            System.out.println("Veri Tabanı bağlanma hatası:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver bulunamadı.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        new ConnectDB();
        return connection;
    }

}
