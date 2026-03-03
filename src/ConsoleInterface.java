package com.fintrack.backend.ui;

import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.repository.FileHandler;
import com.fintrack.backend.service.FinanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleInterface {
    private static final Scanner scanner = new Scanner(System.in, "UTF-8");
    private static FinanceService service;
    private static double monthlyLimit = 10000.0; // Лимит по умолчанию

    public static void main(String[] args) {
        // Настройка кодировки для вывода в консоль Windows
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("Ошибка настройки кодировки");
        }

        FileHandler fileHandler = new FileHandler("data");
        service = new FinanceService(fileHandler);

        System.out.println("=== Добро пожаловать в FinTrack CLI ===");

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addTransaction(true);
                case "2" -> addTransaction(false);
                case "3" -> showBalanceWithPause();
                case "4" -> showAnalytics();
                case "5" -> showHistory();
                case "6" -> deleteTransaction();
                case "7" -> setLimit();
                case "8" -> {
                    System.out.println("Выход из системы. До свидания!");
                    return;
                }
                default -> System.out.println("Ошибка: Выберите пункт от 1 до 8.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- ГЛАВНОЕ МЕНЮ ---");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Показать текущий баланс");
        System.out.println("4. Аналитика (Графики)");
        System.out.println("5. История транзакций");
        System.out.println("6. Удалить транзакцию (по ID)");
        System.out.println("7. Настроить лимит расходов (сейчас: " + monthlyLimit + ")");
        System.out.println("8. Выход");
        System.out.print("Выберите действие: ");
    }

    private static void addTransaction(boolean isIncome) {
        try {
            System.out.print("Введите сумму: ");
            double amount = Double.parseDouble(scanner.nextLine());
            if (!isIncome) amount = -Math.abs(amount);

            System.out.print("Введите категорию: ");
            String category = scanner.nextLine();
            System.out.print("Введите комментарий: ");
            String comment = scanner.nextLine();

            long id = System.currentTimeMillis();
            Transaction t = new Transaction(id, amount, category, LocalDate.now(), comment);
            service.addTransaction(t);

            System.out.println("Успешно добавлено! (ID: " + id + ")");

            if (!isIncome && service.isLimitExceeded(monthlyLimit)) {
                System.err.println("!!! ВНИМАНИЕ: Месячный лимит расходов (" + monthlyLimit + ") превышен! !!!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Введите корректное число.");
        }
    }

    private static void showBalanceWithPause() {
        double balance = service.calculateBalance();
        System.out.printf("\n--- ТЕКУЩИЙ БАЛАНС: %.2f руб. ---\n", balance);
        System.out.println("Нажмите Enter, чтобы вернуться в меню...");
        scanner.nextLine();
    }

    private static void showHistory() {
        List<Transaction> history = service.getAllTransactions(); // Убедись, что этот метод есть в FinanceService
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        System.out.println("\n--- ИСТОРИЯ ТРАНЗАКЦИЙ ---");
        System.out.printf("%-15s | %-10s | %-12s | %s\n", "ID", "Сумма", "Категория", "Комментарий");
        for (Transaction t : history) {
            System.out.printf("%-15d | %-10.2f | %-12s | %s\n", t.getId(), t.getAmount(), t.getCategory(), t.getComment());
        }
    }

    private static void deleteTransaction() {
        try {
            System.out.print("Введите ID транзакции для удаления: ");
            long id = Long.parseLong(scanner.nextLine());
            service.deleteTransaction(id); // Убедись, что метод принимает long id
            System.out.println("Запрос на удаление выполнен.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Неверный формат ID.");
        }
    }

    private static void setLimit() {
        try {
            System.out.print("Введите новый месячный лимит: ");
            monthlyLimit = Double.parseDouble(scanner.nextLine());
            System.out.println("Лимит успешно обновлен.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Введите число.");
        }
    }

    private static void showAnalytics() {
        System.out.println("\n--- АНАЛИТИКА ДОХОДОВ ---");
        drawBarChart(service.getIncomesByCategory()); // Нужно добавить метод в Service

        System.out.println("\n--- АНАЛИТИКА РАСХОДОВ ---");
        drawBarChart(service.getExpensesByCategory());
    }

    private static void drawBarChart(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("[Нет данных]");
            return;
        }
        double max = data.values().stream().map(Math::abs).max(Double::compare).orElse(1.0);
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = Math.abs(entry.getValue());
            int bars = (int) (val / max * 10);
            String chart = "[" + "#".repeat(bars) + " ".repeat(10 - bars) + "]";
            System.out.printf("%-12s %s %.2f руб.\n", entry.getKey() + ":", chart, val);
        }
    }
}