package com.fintrack.backend.service;

import com.fintrack.backend.model.Transaction;
import com.fintrack.backend.repository.FileHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

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
        fileHandler.saveTransactions(transactions); // Сразу сохраняем в файл
    }

    // Удаление транзакции
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

    // Подсчет общего баланса
    public double calculateBalance() {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Группировка расходов по категориям
    public Map<String, Double> getExpensesByCategory() {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0) 
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> Math.abs(t.getAmount()))
                ));
    }

    // Новый метод: группировка доходов по категориям
    public Map<String, Double> getIncomesByCategory() {
        return transactions.stream()
                .filter(t -> t.getAmount() > 0) // Только положительные суммы
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public boolean isLimitExceeded(double monthlyLimit) {
        double currentMonthExpenses = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .filter(t -> t.getDate().getMonth() == LocalDate.now().getMonth() &&
                             t.getDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(t -> Math.abs(t.getAmount()))
                .sum();

        return currentMonthExpenses > monthlyLimit;
    }
}