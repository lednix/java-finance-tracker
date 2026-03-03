package com.fintrack.backend.model;

public enum Currency {
    RUB("₽"), USD("$"), EUR("€");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
