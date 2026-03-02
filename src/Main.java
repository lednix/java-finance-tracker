package com.fintrack.backend;

import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.repository.FileHandler;
import com.fintrack.backend.service.FinanceService;

import java.time.LocalDate;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1. Инициализация (создаем объекты)
        FileHandler fileHandler = new FileHandler("test_data.csv"); // Используем временный файл
        FinanceService service = new FinanceService(fileHandler);

        System.out.println("--- Тестирование Бэкенда ---");

        // 2. Тест: Добавление транзакций
        service.addTransaction(new Transaction(1L, 50000.0, "Зарплата", LocalDate.now(), "Март"));
        service.addTransaction(new Transaction(2L, -1500.0, "Еда", LocalDate.now(), "Обед"));
        service.addTransaction(new Transaction(3L, -300.0, "Транспорт", LocalDate.now(), "Такси"));

        System.out.println("Транзакции добавлены.");

        // 3. Тест: Расчет баланса
        double balance = service.calculateBalance();
        System.out.println("Текущий баланс: " + balance);

        // 4. Тест: Аналитика по категориям
        Map<String, Double> expenses = service.getExpensesByCategory();
        System.out.println("Расходы по категориям: " + expenses);

        // 5. Тест: Проверка лимита (например, лимит 1000)
        boolean limitExceeded = service.isLimitExceeded(1000.0);
        System.out.println("Лимит превышен: " + limitExceeded);

        System.out.println("--- Тест завершен. Файл test_data.csv создан. ---");
    }
}
