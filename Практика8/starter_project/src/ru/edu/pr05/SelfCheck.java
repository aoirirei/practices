package ru.edu.pr05;

import java.time.LocalDate;
import java.util.List;

public class SelfCheck {
    private static int passed;
    private static int failed;
    private static final LocalDate START = LocalDate.of(2026, 10, 10);
    private static final LocalDate END = LocalDate.of(2026, 10, 12);
    private static void test(String name, Runnable body) {
        try { body.run(); passed++; System.out.println("PASS " + name); }
        catch (Throwable e) { failed++; System.out.println("FAIL " + name + ": " + e); }
    }
    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("Условие не выполнено");
    }
    private static void expect(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable e) {
            if (type.isInstance(e)) return;
            throw new AssertionError("Другой тип ошибки: " + e);
        }
        throw new AssertionError("Не получено исключение " + type.getSimpleName());
    }
    private static BusinessTrip tripAt(TripStatus target) {
        BusinessTrip trip = new BusinessTrip("T1", START, END);
        if (target == TripStatus.DRAFT) return trip;
        trip.submit();
        if (target == TripStatus.SUBMITTED) return trip;
        if (target == TripStatus.REJECTED) { trip.reject("1234567890"); return trip; }
        if (target == TripStatus.CANCELLED) { trip.cancel(); return trip; }
        trip.approve();
        if (target == TripStatus.COMPLETED) trip.complete(END.plusDays(1));
        return trip;
    }
    private static void action(BusinessTrip trip, int action) {
        switch (action) {
            case 0: trip.submit(); break;
            case 1: trip.approve(); break;
            case 2: trip.reject("1234567890"); break;
            case 3: trip.cancel(); break;
            case 4: trip.complete(END.plusDays(1)); break;
            default: throw new AssertionError("Неизвестное действие");
        }
    }
    public static void main(String[] args) {
        TripStatus[] states = {TripStatus.DRAFT, TripStatus.SUBMITTED, TripStatus.APPROVED,
                TripStatus.REJECTED, TripStatus.CANCELLED, TripStatus.COMPLETED};
        // Колонки: submit, approve, reject, cancel, complete. null = запрет перехода.
        TripStatus[][] expected = {
            {TripStatus.SUBMITTED, null, null, TripStatus.CANCELLED, null},
            {null, TripStatus.APPROVED, TripStatus.REJECTED, TripStatus.CANCELLED, null},
            {null, null, null, TripStatus.CANCELLED, TripStatus.COMPLETED},
            {null, null, null, null, null},
            {null, null, null, null, null},
            {null, null, null, null, null}
        };
        String[] names = {"submit", "approve", "reject", "cancel", "complete"};
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < names.length; j++) {
                final TripStatus from = states[i]; final TripStatus to = expected[i][j]; final int op = j;
                test(from + " / " + names[j], () -> {
                    BusinessTrip trip = tripAt(from);
                    String reason = trip.getRejectionReason();
                    if (to == null) {
                        expect(IllegalStateException.class, () -> action(trip, op));
                        check(trip.getStatus() == from);
                        check(java.util.Objects.equals(reason, trip.getRejectionReason()));
                    } else { action(trip, op); check(trip.getStatus() == to); }
                });
            }
        }
        test("null id", () -> expect(IllegalArgumentException.class, () -> new BusinessTrip(null, START, END)));
        test("blank id", () -> expect(IllegalArgumentException.class, () -> new BusinessTrip(" ", START, END)));
        test("null start", () -> expect(IllegalArgumentException.class, () -> new BusinessTrip("T", null, END)));
        test("null end", () -> expect(IllegalArgumentException.class, () -> new BusinessTrip("T", START, null)));
        test("end before start", () -> expect(IllegalArgumentException.class, () -> new BusinessTrip("T", END, START)));
        test("same day allowed", () -> { BusinessTrip t = new BusinessTrip("T", START, START); t.submit(); check(t.getStatus() == TripStatus.SUBMITTED); });
        test("id normalized", () -> check(new BusinessTrip(" T ", START, END).getId().equals("T")));
        test("initial reason", () -> check(tripAt(TripStatus.DRAFT).getRejectionReason() == null));
        test("reason null rollback", () -> { BusinessTrip t = tripAt(TripStatus.SUBMITTED); expect(IllegalArgumentException.class, () -> t.reject(null)); check(t.getStatus() == TripStatus.SUBMITTED && t.getRejectionReason() == null); });
        test("reason blank", () -> expect(IllegalArgumentException.class, () -> tripAt(TripStatus.SUBMITTED).reject("          ")));
        test("reason 9", () -> expect(IllegalArgumentException.class, () -> tripAt(TripStatus.SUBMITTED).reject("123456789")));
        test("reason 10", () -> { BusinessTrip t = tripAt(TripStatus.SUBMITTED); t.reject("1234567890"); check(t.getStatus() == TripStatus.REJECTED && t.getRejectionReason().equals("1234567890")); });
        test("reason 11", () -> { BusinessTrip t = tripAt(TripStatus.SUBMITTED); t.reject("12345678901"); check(t.getStatus() == TripStatus.REJECTED); });
        test("reason 9 plus spaces", () -> expect(IllegalArgumentException.class, () -> tripAt(TripStatus.SUBMITTED).reject(" 123456789 ")));
        test("reason stripped", () -> { BusinessTrip t = tripAt(TripStatus.SUBMITTED); t.reject(" 1234567890 "); check(t.getRejectionReason().equals("1234567890")); });
        test("complete day before rollback", () -> { BusinessTrip t = tripAt(TripStatus.APPROVED); expect(IllegalArgumentException.class, () -> t.complete(END.minusDays(1))); check(t.getStatus() == TripStatus.APPROVED); });
        test("complete exact end", () -> expect(IllegalArgumentException.class, () -> tripAt(TripStatus.APPROVED).complete(END)));
        test("complete day after", () -> { BusinessTrip t = tripAt(TripStatus.APPROVED); t.complete(END.plusDays(1)); check(t.getStatus() == TripStatus.COMPLETED); });
        test("complete null", () -> expect(IllegalArgumentException.class, () -> tripAt(TripStatus.APPROVED).complete(null)));
        test("service duplicate id", () -> { BusinessTripService s = new BusinessTripService(); BusinessTrip t = s.create("T", START, END); expect(IllegalArgumentException.class, () -> s.create(" T ", START, END)); check(s.getById("T") == t); });
        test("service get missing", () -> expect(IllegalArgumentException.class, () -> new BusinessTripService().getById("missing")));
        test("service get blank", () -> expect(IllegalArgumentException.class, () -> new BusinessTripService().getById(" ")));
        test("service find null", () -> expect(IllegalArgumentException.class, () -> new BusinessTripService().findByStatus(null)));
        test("service filter live state", () -> { BusinessTripService s = new BusinessTripService(); BusinessTrip t = s.create("T", START, END); s.create("T2", START, END); t.submit(); check(s.findByStatus(TripStatus.SUBMITTED).equals(List.of(t)) && s.findByStatus(TripStatus.DRAFT).size() == 1); });
        test("service list unmodifiable", () -> { BusinessTripService s = new BusinessTripService(); s.create("T", START, END); expect(UnsupportedOperationException.class, () -> s.findByStatus(TripStatus.DRAFT).clear()); check(s.findByStatus(TripStatus.DRAFT).size() == 1); });
        System.out.println("Passed: " + passed + "; failed: " + failed);
        if (failed != 0) throw new AssertionError("SelfCheck failed");
    }
}
