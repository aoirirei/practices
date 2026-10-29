package ru.edu.pr02;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            System.out.println("Введите часы за 5 дней через ; (вариант 1)");
            try {
                String line = scanner.hasNextLine() ? scanner.nextLine() : null;
                int[] hours = TimesheetAnalyzer.parseHours(line);
                int sum = TimesheetAnalyzer.total(hours);
                double mean = TimesheetAnalyzer.average(hours);
                int days = TimesheetAnalyzer.overtimeDays(hours);
                int weekly = TimesheetAnalyzer.weeklyOvertime(hours);
                System.out.println("Сумма часов: " + sum);
                System.out.printf(Locale.ROOT, "Среднее: %.2f%n", mean);
                System.out.println("Дней с переработкой: " + days);
                System.out.println("Недельная переработка: " + weekly);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
