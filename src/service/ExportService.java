package com.fintrack.backend.service;

import com.fintrack.backend.model.Transaction;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportService {

    public void exportToHtml(List<Transaction> transactions, double balance) {
        String fileName = "finance_report.html";
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='ru'><head><meta charset='UTF-8'>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f4f7f6; color: #333; padding: 20px; }");
            out.println(".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 900px; margin: auto; }");
            out.println("h1 { color: #2c3e50; border-bottom: 2px solid #27ae60; padding-bottom: 10px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #27ae60; color: white; }");
            out.println(".income { color: #27ae60; font-weight: bold; }");
            out.println(".expense { color: #e74c3c; font-weight: bold; }");
            out.println(".balance-card { background: #2c3e50; color: white; padding: 15px; border-radius: 5px; display: inline-block; margin-top: 20px; }");
            out.println("</style></head><body>");

            out.println("<div class='container'>");
            out.println("<h1>FinTrack: Финансовый отчет</h1>");
            out.println("<p>Дата генерации: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "</p>");

            out.println("<table>");
            out.println("<thead><tr><th>ID</th><th>Дата</th><th>Категория</th><th>Сумма</th><th>Валюта</th></tr></thead><tbody>");

            for (Transaction t : transactions) {
                String amountClass = t.getAmount() >= 0 ? "income" : "expense";
                out.println("<tr>");
                out.println("<td>" + t.getId() + "</td>");
                out.println("<td>" + t.getDate() + "</td>");
                out.println("<td>" + t.getCategory() + "</td>");
                out.println("<td class='" + amountClass + "'>" + String.format("%.2f", t.getAmount()) + "</td>");
                out.println("<td>" + t.getCurrency() + "</td>");
                out.println("</tr>");
            }

            out.println("</tbody></table>");
            out.println("<div class='balance-card'>Общий баланс: " + String.format("%.2f", balance) + " RUB</div>");
            out.println("</div></body></html>");

            System.out.println("\n[OK] Отчет успешно создан: " + fileName);
        } catch (IOException e) {
            System.out.println("\n[!] Ошибка при создании отчета: " + e.getMessage());
        }
    }
}
