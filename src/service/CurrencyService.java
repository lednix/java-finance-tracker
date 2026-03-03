package com.fintrack.backend.service;

import com.fintrack.backend.model.Currency;
import java.util.HashMap;
import java.util.Map;

public class CurrencyService {
    // Храним курсы валют относительно базовой (например, RUB)
    private final Map<Currency, Double> rates = new HashMap<>();

    public CurrencyService() {
        // Устанавливаем постоянный курс (позже можно будет брать из API)
        rates.put(Currency.RUB, 1.0);
        rates.put(Currency.USD, 91.5);
        rates.put(Currency.EUR, 99.0); 
    }

    /**
     * Конвертирует сумму из любой валюты в рубли 
     */
    public double convertToBase(double amount, Currency from) {
        return amount * rates.get(from);
    }

    /**
     * Позволяет получить список всех доступных курсов
     */
    public Map<Currency, Double> getRates() {
        return rates;
    }
}
