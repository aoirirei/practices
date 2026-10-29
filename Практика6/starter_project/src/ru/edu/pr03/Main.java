package ru.edu.pr03;

import java.util.Locale;
import java.util.Scanner;

/** Консоль принимает несколько заявок до команды exit. */
public class Main {
    private static boolean parseBoolean(String text) {
        String value = text.trim();
        if (value.equalsIgnoreCase("true")) return true;
        if (value.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException("Логическое значение должно быть true или false");
    }

    public static void main(String[] args) {
        ApprovalRouter router = new ApprovalRouter();
        System.out.println("Вариант 1. Финансовый порог: " + ApprovalRouter.FINANCE_LIMIT);
        System.out.println("Формат: amount;urgent;risk;personal. Для выхода: exit");
        System.out.println("Пример: 9;true;LOW;false");
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("exit")) break;
                try {
                    String[] fields = line.split(";", -1);
                    if (fields.length != 4) throw new IllegalArgumentException("Нужно ровно 4 поля");
                    long amount;
                    try { amount = Long.parseLong(fields[0].trim()); }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Сумма должна быть целым числом типа long");
                    }
                    boolean urgent = parseBoolean(fields[1]);
                    RiskLevel risk;
                    try { risk = RiskLevel.valueOf(fields[2].trim().toUpperCase(Locale.ROOT)); }
                    catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Риск должен быть LOW, MEDIUM или HIGH");
                    }
                    boolean personal = parseBoolean(fields[3]);
                    ApprovalRoute result = router.route(amount, urgent, risk, personal);
                    System.out.println("Маршрут: " + result);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        System.out.println("Программа завершена");
    }
}
