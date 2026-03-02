package com.fintrack.backend.service;

import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.repository.FileHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceService {
    private List<Transaction> transactions;
    private FileHandler fileHandler;

    public FinanceService(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        // Загружаем данные при инициализации сервиса
        this.transactions = fileHandler.loadTransactions();
    }

    // Добавление транзакции
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        fileHandler.saveTransactions(transactions); // Сразу сохраняем в файл чтобы потом не было мороки
    }

    // Удаление транзакции при необходимости
    public boolean deleteTransaction(Long id) {
        boolean removed = transactions.removeIf(t -> t.getId().equals(id));
        if (removed) {
            fileHandler.saveTransactions(transactions); // Сохраняем после удаления
        }
        return removed;
    }

    // Получить все транзакции
    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    // аналитика чтоб не заморачиваться

    // Подсчет общего баланса (Доходы - Расходы)
    // Примечание: сумма < 0 считается расходом
    public double calculateBalance() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Группировка расходов по категориям
    public Map<String, Double> getExpensesByCategory() {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0) // Берем только расходы
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> Math.abs(t.getAmount())) // Суммируем модули
                ));
    }
}
