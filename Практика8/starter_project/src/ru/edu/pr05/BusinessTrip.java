package ru.edu.pr05;

import java.time.LocalDate;

public final class BusinessTrip {
    public static final int MIN_REASON_LENGTH = 10;
    private final String id;
    private final LocalDate start;
    private final LocalDate end;
    private TripStatus status = TripStatus.DRAFT;
    private String rejectionReason;

    public BusinessTrip(String id, LocalDate start, LocalDate end) {
        validateData(id, start, end);
        this.id = id.strip(); this.start = start; this.end = end;
    }
    private static void validateData(String id, LocalDate start, LocalDate end) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Нужен id заявки");
        if (start == null || end == null) throw new IllegalArgumentException("Нужны обе даты");
        if (end.isBefore(start)) throw new IllegalArgumentException("Дата окончания раньше начала");
    }
    private void requireStatus(TripStatus expected) {
        if (status != expected)
            throw new IllegalStateException("Нужен статус " + expected + ", текущий: " + status);
    }
    public void submit() {
        requireStatus(TripStatus.DRAFT);
        validateData(id, start, end);
        status = TripStatus.SUBMITTED;
    }
    public void approve() {
        requireStatus(TripStatus.SUBMITTED);
        status = TripStatus.APPROVED;
    }
    public void reject(String reason) {
        requireStatus(TripStatus.SUBMITTED);
        if (reason == null || reason.strip().length() < MIN_REASON_LENGTH)
            throw new IllegalArgumentException("Причина должна содержать минимум " + MIN_REASON_LENGTH + " символов");
        rejectionReason = reason.strip();
        status = TripStatus.REJECTED;
    }
    public void cancel() {
        if (status != TripStatus.DRAFT && status != TripStatus.SUBMITTED && status != TripStatus.APPROVED)
            throw new IllegalStateException("Отмена недоступна из " + status);
        status = TripStatus.CANCELLED;
    }
    public void complete(LocalDate today) {
        requireStatus(TripStatus.APPROVED);
        if (today == null) throw new IllegalArgumentException("Текущая дата не задана");
        if (!today.isAfter(end))
            throw new IllegalArgumentException("Завершение возможно только после даты окончания");
        status = TripStatus.COMPLETED;
    }
    public String getId() { return id; }
    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }
    public TripStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
}
