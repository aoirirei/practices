package ru.edu.pr02;

/** Обработка табеля. Вариант 1: дневная норма 10 часов. */
public class TimesheetAnalyzer {
    public static final int DAYS = 5;
    public static final int MIN_HOURS = 0;
    public static final int MAX_HOURS = 16;
    public static final int DAILY_NORM = 10;
    public static final int WEEKLY_NORM = 40;

    public static int[] parseHours(String line) {
        if (line == null) throw new IllegalArgumentException("Строка не задана");
        String[] parts = line.split(";", -1);
        if (parts.length != DAYS) {
            throw new IllegalArgumentException("Нужно ровно " + DAYS
                    + " значений; получено " + parts.length);
        }
        int[] hours = new int[DAYS];
        for (int i = 0; i < parts.length; i++) {
            String value = parts[i].trim();
            // Только десятичные цифры и необязательный знак.
            if (!value.matches("[+-]?[0-9]+")) {
                throw new IllegalArgumentException("Позиция " + (i + 1)
                        + ": нужно целое число");
            }
            try {
                hours[i] = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Позиция " + (i + 1)
                        + ": число не помещается в int");
            }
            checkHour(hours[i], i);
        }
        return hours;
    }

    private static void checkHour(int hour, int index) {
        if (hour < MIN_HOURS || hour > MAX_HOURS) {
            throw new IllegalArgumentException("Позиция " + (index + 1)
                    + ": часы должны быть от " + MIN_HOURS + " до " + MAX_HOURS);
        }
    }

    private static void validateHours(int[] hours) {
        if (hours == null || hours.length != DAYS) {
            throw new IllegalArgumentException("Нужен массив из " + DAYS + " значений");
        }
        for (int i = 0; i < hours.length; i++) checkHour(hours[i], i);
    }

    public static int total(int[] hours) {
        validateHours(hours);
        int sum = 0;
        for (int hour : hours) sum += hour;
        return sum;
    }

    public static double average(int[] hours) {
        return (double) total(hours) / DAYS;
    }

    public static int overtimeDays(int[] hours) {
        validateHours(hours);
        int count = 0;
        for (int hour : hours) {
            if (hour > DAILY_NORM) count++;
        }
        return count;
    }

    public static int weeklyOvertime(int[] hours) {
        return Math.max(0, total(hours) - WEEKLY_NORM);
    }
}
