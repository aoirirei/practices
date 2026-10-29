package ru.edu.pr03;

public class SelfCheck {
    private static int passed;
    private static int failed;
    private static final ApprovalRouter ROUTER = new ApprovalRouter();

    private static void check(String name, long amount, boolean urgent,
                              RiskLevel risk, boolean personal, ApprovalRoute expected) {
        try {
            ApprovalRoute actual = ROUTER.route(amount, urgent, risk, personal);
            boolean ok = actual == expected;
            System.out.println((ok ? "PASS " : "FAIL ") + name + " -> " + actual);
            if (ok) passed++; else failed++;
        } catch (Exception e) { failed++; System.out.println("FAIL " + name + ": " + e); }
    }

    private static void invalid(String name, long amount, RiskLevel risk) {
        try {
            ROUTER.route(amount, true, risk, true);
            failed++; System.out.println("FAIL " + name);
        } catch (IllegalArgumentException e) {
            passed++; System.out.println("PASS " + name + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        check("below", 9, false, RiskLevel.LOW, false, ApprovalRoute.STANDARD);
        check("equal", 10, false, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("above", 11, false, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("urgent below", 9, true, RiskLevel.LOW, false, ApprovalRoute.FAST_TRACK);
        check("urgent equal", 10, true, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("zero", 0, false, RiskLevel.LOW, false, ApprovalRoute.STANDARD);
        check("urgent zero", 0, true, RiskLevel.MEDIUM, false, ApprovalRoute.FAST_TRACK);
        check("medium", 9, false, RiskLevel.MEDIUM, false, ApprovalRoute.STANDARD);
        check("security over all", 500000, true, RiskLevel.HIGH, true, ApprovalRoute.SECURITY_REVIEW);
        check("security at zero", 0, false, RiskLevel.LOW, true, ApprovalRoute.SECURITY_REVIEW);
        check("director over finance", 500000, true, RiskLevel.HIGH, false, ApprovalRoute.DIRECTOR_REVIEW);
        check("director over urgent", 9, true, RiskLevel.HIGH, false, ApprovalRoute.DIRECTOR_REVIEW);
        check("original 499999", 499999, false, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("original 500000", 500000, false, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("original 500001", 500001, false, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("max long", Long.MAX_VALUE, true, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("sample 120000", 120000, true, RiskLevel.LOW, false, ApprovalRoute.FINANCE_REVIEW);
        check("sample high", 10000, true, RiskLevel.HIGH, false, ApprovalRoute.DIRECTOR_REVIEW);
        check("sample personal", 10000, true, RiskLevel.LOW, true, ApprovalRoute.SECURITY_REVIEW);
        invalid("negative", -1, RiskLevel.LOW);
        invalid("min long", Long.MIN_VALUE, RiskLevel.HIGH);
        invalid("null risk", 9, null);
        System.out.println("Passed: " + passed + "; failed: " + failed);
        if (failed != 0) throw new AssertionError("SelfCheck failed");
    }
}
