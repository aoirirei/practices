package ru.edu.pr02;

public class SelfCheck {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "PASS " : "FAIL ") + name);
        if (ok) passed++; else failed++;
    }
    private static void valid(String name, String line, int sum,
                              double mean, int days, int weekly) {
        try {
            int[] h = TimesheetAnalyzer.parseHours(line);
            check(name, TimesheetAnalyzer.total(h) == sum
                    && Math.abs(TimesheetAnalyzer.average(h) - mean) < 1e-9
                    && TimesheetAnalyzer.overtimeDays(h) == days
                    && TimesheetAnalyzer.weeklyOvertime(h) == weekly);
        } catch (Exception e) { check(name + ": " + e.getMessage(), false); }
    }
    private static void invalid(String name, String line, String message) {
        try { TimesheetAnalyzer.parseHours(line); check(name, false); }
        catch (IllegalArgumentException e) {
            check(name, e.getMessage().contains(message));
        }
    }
    public static void main(String[] args) {
        valid("ordinary", "8;8;8;8;8", 40, 8, 0, 0);
        valid("lower", "0;0;0;0;0", 0, 0, 0, 0);
        valid("upper", "16;16;16;16;16", 80, 16, 5, 40);
        valid("daily equality", "10;10;10;10;10", 50, 10, 0, 10);
        valid("daily above", "11;10;9;0;0", 30, 6, 1, 0);
        valid("weekly below", "8;8;8;8;7", 39, 7.8, 0, 0);
        valid("weekly above", "9;8;8;8;8", 41, 8.2, 0, 1);
        valid("spaces", " 8 ; 9;10 ;11; 12 ", 50, 10, 2, 10);
        valid("old sample new rule", "9;8;7;10;8", 42, 8.4, 0, 2);
        invalid("empty", "", "ровно 5");
        invalid("null", null, "не задана");
        invalid("17", "8;8;17;8;8", "Позиция 3");
        invalid("negative", "-1;8;8;8;8", "Позиция 1");
        invalid("text", "8;abc;8;8;8", "Позиция 2");
        invalid("four", "8;8;8;8", "ровно 5");
        invalid("six", "8;8;8;8;8;8", "ровно 5");
        invalid("trailing empty", "8;8;8;8;", "Позиция 5");
        invalid("middle empty", "8;;8;8;8", "Позиция 2");
        invalid("fraction", "8;8;1.5;8;8", "Позиция 3");
        invalid("overflow", "8;8;8;8;999999999999", "Позиция 5");
        try { TimesheetAnalyzer.total(new int[]{8}); check("short array", false); }
        catch (IllegalArgumentException e) { check("short array", true); }
        System.out.println("Passed: " + passed + "; failed: " + failed);
        if (failed > 0) throw new AssertionError("SelfCheck failed");
    }
}
