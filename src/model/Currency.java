package com.fintrack.backend.model;

public enum Currency { // добавил что бы не было путаницы между "USD" "usd" и тд
    RUB("₽"), USD("$"), EUR("€");

    private final String symbol;
    Currency(String symbol) { this.symbol = symbol; }
    public String getSymbol() { return symbol; }
}
