package ru.edu.pr03;

/** Вариант 1: параметр 10 трактуется как значение финансового порога. */
public class ApprovalRouter {
    public static final long FINANCE_LIMIT = 10L;

    public ApprovalRoute route(long amount, boolean urgent, RiskLevel risk,
                               boolean containsPersonalData) {
        validate(amount, risk);
        if (containsPersonalData) return ApprovalRoute.SECURITY_REVIEW;
        if (risk == RiskLevel.HIGH) return ApprovalRoute.DIRECTOR_REVIEW;
        if (amount >= FINANCE_LIMIT) return ApprovalRoute.FINANCE_REVIEW;
        if (urgent) return ApprovalRoute.FAST_TRACK;
        return ApprovalRoute.STANDARD;
    }

    private void validate(long amount, RiskLevel risk) {
        if (amount < 0) throw new IllegalArgumentException("Сумма не может быть отрицательной");
        if (risk == null) throw new IllegalArgumentException("Уровень риска не задан");
    }
}
