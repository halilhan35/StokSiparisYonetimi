package org.example.generator;

import org.example.classes.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomerGenerator {

    private static final Random rand = new Random();

    // Şifre oluşturma metodu
    private static String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return password.toString();
    }

    // Müşteri oluşturma
    public static List<Customer> generateCustomers() {
        List<Customer> customers = new ArrayList<>();
        int numberOfCustomers = rand.nextInt(6) + 5; // 5-10 arası müşteri sayısı
        int premiumCustomers = 0;

        for (int i = 0; i < numberOfCustomers; i++) {
            // Müşteri tipi seçimi: en az 2 premium müşteri olacak şekilde
            String customerType = (premiumCustomers < 2 || rand.nextBoolean()) ? "Premium" : "Standard";
            if (customerType.equals("Premium")) premiumCustomers++;

            // Bütçe random: 500-3000 arasında
            double budget = rand.nextInt(2501) + 500; // 500-3000 arası bütçe
            double totalSpent = rand.nextInt(1001); // toplam harcama random (0-1000 TL arası)

            // Müşteri ismi (Örnek isim)
            String customerName = "Customer" + (i + 1);

            // Müşteri ID (benzersiz bir ID numarası oluşturulabilir)
            int customerID = i + 1;

            // Rastgele şifre oluşturma
            String customerPassword = generatePassword(8); // 8 karakter uzunluğunda şifre

            // Müşteri nesnesi oluşturma ve listeye ekleme
            customers.add(new Customer(customerID, customerName, customerPassword, budget, customerType, totalSpent));
        }

        return customers;
    }
}
