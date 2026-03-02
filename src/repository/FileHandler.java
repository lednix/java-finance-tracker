package com.fintrack.backend.repository;

import com.fintrack.backend.model.Category;
import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.model.User;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Путь к директории или префикс для файлов
    private final String baseFileName;

    // --- ДОБАВЬ ЭТОТ КОНСТРУКТОР ---
    public FileHandler(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    // Вспомогательный метод для генерации имен файлов
    private String getFilePath(String type) {
        return baseFileName + "_" + type + ".csv";
    }

    // --- ЛОГИКА ДЛЯ TRANSACTION ---
    public void saveTransactions(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("transactions")))) {
            for (Transaction t : transactions) {
                writer.println(t.getId() + "," + t.getAmount() + "," + t.getCategory() + "," + t.getDate() + "," + t.getComment());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении транзакций: " + e.getMessage());
        }
    }

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(getFilePath("transactions"));
        if (!file.exists()) return transactions;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    transactions.add(new Transaction(
                            Long.parseLong(parts[0]),
                            Double.parseDouble(parts[1]),
                            parts[2],
                            LocalDate.parse(parts[3]),
                            parts[4]
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении транзакций: " + e.getMessage());
        }
        return transactions;
    }

    // --- ЛОГИКА ДЛЯ CATEGORY ---
    public void saveCategories(List<Category> categories) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("categories")))) {
            for (Category c : categories) {
                writer.println(c.getId() + "," + c.getName());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении категорий: " + e.getMessage());
        }
    }

    public List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        File file = new File(getFilePath("categories"));
        if (!file.exists()) return categories;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    categories.add(new Category(Long.parseLong(parts[0]), parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении категорий: " + e.getMessage());
        }
        return categories;
    }

    // --- ЛОГИКА ДЛЯ USER ---
    public void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getFilePath("users")))) {
            for (User u : users) {
                writer.println(u.getId() + "," + u.getUsername() + "," + u.getPassword());
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении пользователей: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(getFilePath("users"));
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(Long.parseLong(parts[0]), parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении пользователей: " + e.getMessage());
        }
        return users;
    }
}
