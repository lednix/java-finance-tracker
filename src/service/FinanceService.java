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
    private CurrencyService currencyService;

    public FinanceService(FileHandler fileHandler, CurrencyService currencyService) {
        this.fileHandler = fileHandler;
        this.currencyService = currencyService;
        this.transactions = fileHandler.loadTransactions();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        fileHandler.saveTransactions(transactions);
    }

    public boolean deleteTransaction(Long id) {
        boolean removed = transactions.removeIf(t -> t.getId().equals(id));
        if (removed) {
            fileHandler.saveTransactions(transactions);
        }
        return removed;
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    // счет баланса уже с конвертацией
    public double calculateBalance() {
        return transactions.stream()
                .mapToDouble(t -> currencyService.convertToBase(t.getAmount(), t.getCurrency()))
                .sum();
    }

    // аналитика расходов с конвертацией
    public Map<String, Double> getExpensesByCategory() {
        return transactions.stream()
                .filter(t -> t.getAmount() < 0) 
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> 
                            currencyService.convertToBase(Math.abs(t.getAmount()), t.getCurrency()))
                ));
    }

    public Map<String, Double> getIncomesByCategory() {
        return transactions.stream()
                .filter(t -> t.getAmount() > 0)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(t -> 
                            currencyService.convertToBase(t.getAmount(), t.getCurrency()))
                ));
    }

    public boolean isLimitExceeded(double monthlyLimit) {
        double currentMonthExpenses = transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .filter(t -> t.getDate().getMonth() == LocalDate.now().getMonth() &&
                             t.getDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(t -> currencyService.convertToBase(Math.abs(t.getAmount()), t.getCurrency()))
                .sum();

        return currentMonthExpenses > monthlyLimit;
    }
}
