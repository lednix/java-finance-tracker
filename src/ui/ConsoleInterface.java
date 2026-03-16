package com.fintrack.backend.ui;

import com.fintrack.backend.model.Currency;
import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.repository.FileHandler;
import com.fintrack.backend.service.CurrencyService;
import com.fintrack.backend.service.FinanceService;
import com.fintrack.backend.service.ExportService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleInterface {
    private static final Scanner scanner = new Scanner(System.in, "UTF-8");
    private static FinanceService service;
    private static CurrencyService currencyService;
    private static ExportService exportService;
    private static double monthlyLimit = 10000.0;

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("Ошибка кодировки");
        }

        FileHandler fileHandler = new FileHandler("data");
        currencyService = new CurrencyService(); 
        service = new FinanceService(fileHandler, currencyService);
        exportService = new ExportService();

        while (true) {
            clearConsole();
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addTransaction(true);
                case "2" -> addTransaction(false);
                case "3" -> showBalanceWithPause();
                case "4" -> { showAnalytics(); waitEnter(); }
                case "5" -> { showHistory(); waitEnter(); }
                case "6" -> deleteTransaction();
                case "7" -> setLimit();
                case "8" -> { System.out.println("Выход..."); return; }
                case "9" -> { 
                  exportService.exportToHtml(service.getAllTransactions(), service.calculateBalance()); 
                  waitEnter(); 
                }
                default -> { System.out.println("Ошибка выбора."); waitEnter(); }
            }
        }
    }

    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for(int i = 0; i < 30; i++) System.out.println();
        }
    }

    private static void printMenu() {
        double spent = service.getAllTransactions().stream()
                .filter(t -> t.getAmount() < 0)
                .filter(t -> t.getDate().getMonth() == LocalDate.now().getMonth())
                .mapToDouble(t -> Math.abs(currencyService.convertToBase(t.getAmount(), t.getCurrency())))
                .sum();
        
        double remaining = monthlyLimit - spent;

        System.out.println(Colors.CYAN + "=== FinTrack CLI ===" + Colors.RESET);
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Баланс (в RUB)");
        System.out.println("4. Аналитика");
        System.out.println("5. История");
        System.out.println("6. Удалить транзакцию");
       
        if (remaining < 0) {
          System.out.printf("7. Лимит (Траты: %.2f | " + Colors.RED + "ПЕРЕРАСХОД: %.2f !!!" + Colors.RESET + ")\n", 
            spent, Math.abs(remaining));
        } else {
          System.out.printf("7. Лимит (Траты: %.2f | " + Colors.GREEN + "Остаток: %.2f" + Colors.RESET + ")\n", 
            spent, remaining);
        }

        System.out.println("8. Выход");
        System.out.println("9. Создание HTML");
        System.out.print("\nВыберите действие: ");
    }

    private static void addTransaction(boolean isIncome) {
      try {
        System.out.print("Сумма: ");
        double amount = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Валюта (1-RUB, 2-USD, 3-EUR): ");
        String currChoice = scanner.nextLine();
        Currency currency = switch(currChoice) {
            case "2" -> Currency.USD;
            case "3" -> Currency.EUR;
            default -> Currency.RUB;
        };

        double amountInRub = currencyService.convertToBase(amount, currency);
        double currentBalance = service.calculateBalance();

          if (!isIncome && amountInRub > currentBalance) {
              System.out.println("\n[ОШИБКА] Недостаточно средств на балансе!");
              System.out.printf("Доступно: %.2f руб. | Требуется: %.2f руб.\n", currentBalance, amountInRub);
              waitEnter();
              return; // Прерываем метод, транзакция не добавится
          }

          System.out.print("Категория: ");
          String category = scanner.nextLine();

          long id = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(9));
          double finalAmount = isIncome ? Math.abs(amount) : -Math.abs(amount);
        
          Transaction t = new Transaction(id, finalAmount, category, LocalDate.now(), "", currency);
          service.addTransaction(t);
        
          System.out.println("Успешно добавлено! ID: " + id);

          // Предупреждение о лимите (бюджете) остается
          if (!isIncome && service.isLimitExceeded(monthlyLimit)) {
            System.out.println(Colors.RED + Colors.BOLD + "[!!!] ВНИМАНИЕ: Лимит превышен!" + Colors.RESET);
          }

          waitEnter();
      } catch (Exception e) {
          System.out.println("Ошибка ввода.");
          waitEnter();
      }
    }


    private static void showHistory() {
      System.out.println(Colors.BLUE + "\n--- ИСТОРИЯ ---" + Colors.RESET);
      System.out.printf(Colors.BOLD + "%-6s | %-12s | %-12s | %-5s\n" + Colors.RESET, "ID", "Сумма", "Категория", "Вал.");
      System.out.println("----------------------------------------------");
      for (Transaction t : service.getAllTransactions()) {
        // Выбираем цвет в зависимости от суммы
          String amountColor = t.getAmount() < 0 ? Colors.RED : Colors.GREEN;
        
          System.out.printf(Colors.PURPLE + "%-6d" + Colors.RESET + " | " + amountColor + "%-12.2f" + Colors.RESET + " | %-12s | %-5s\n", 
              t.getId(), t.getAmount(), t.getCategory(), t.getCurrency());
      }
    }

    private static void showAnalytics() {
        System.out.println("\n--- РАСХОДЫ ПО КАТЕГОРИЯМ (в RUB) ---");
        drawBarChart(service.getExpensesByCategory());
        System.out.println("\n--- ДОХОДЫ ПО КАТЕГОРИЯМ (в RUB) ---");
        drawBarChart(service.getIncomesByCategory());
    }

    private static void drawBarChart(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            System.out.println("[Нет данных]");
            return;
        }
        double max = data.values().stream().map(Math::abs).max(Double::compare).orElse(1.0);
        data.forEach((cat, val) -> {
            int bars = (int) (Math.abs(val) / max * 10);
            String chart = "█".repeat(bars) + " ".repeat(10 - bars);
            System.out.printf("%-10s | %s (%.2f руб.)\n", cat, chart, Math.abs(val));
        });
    }

    private static void waitEnter() {
        System.out.println("\nНажмите Enter...");
        scanner.nextLine();
    }

    private static void showBalanceWithPause() {
        System.out.printf("\nОБЩИЙ БАЛАНС: %.2f руб.\n", service.calculateBalance());
        waitEnter();
    }

    private static void setLimit() {
        System.out.print("Новый лимит (в RUB): ");
        try {
            monthlyLimit = Double.parseDouble(scanner.nextLine());
            System.out.println("Лимит успешно обновлен.");
        } catch (Exception e) { System.out.println("Ошибка формата."); }
        waitEnter();
    }

    private static void deleteTransaction() {
        System.out.print("Введите ID для удаления: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            if (service.deleteTransaction(id)) System.out.println("Транзакция удалена.");
            else System.out.println("Не найдена.");
        } catch (Exception e) { System.out.println("Ошибка формата."); }
        waitEnter();
    }
}
