package com.fintrack.backend.model;

import java.time.LocalDate;

public class Transaction {
    private Long id;
    private double amount;
    private String category;
    private LocalDate date;
    private String comment;

    // Конструктор
    public Transaction(Long id, double amount, String category, LocalDate date, String comment) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.comment = comment;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                '}';
    }
}
