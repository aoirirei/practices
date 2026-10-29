package ru.edu.pr05;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BusinessTripService service = new BusinessTripService();
        BusinessTrip current = null;
        System.out.println("create;id;start;end | select;id | submit | approve | reject;reason");
        System.out.println("cancel | complete[;testDate] | show | find;STATUS | exit");
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            while (scanner.hasNextLine()) {
                String[] fields = scanner.nextLine().split(";", -1);
                String command = fields[0].strip().toLowerCase(Locale.ROOT);
                try {
                    switch (command) {
                        case "create":
                            requireFields(fields, 4);
                            current = service.create(fields[1], parseDate(fields[2]), parseDate(fields[3]));
                            System.out.println("Создана " + current.getId()); break;
                        case "select":
                            requireFields(fields, 2); current = service.getById(fields[1]);
                            System.out.println("Выбрана " + current.getId()); break;
                        case "find":
                            requireFields(fields, 2);
                            TripStatus status;
                            try { status = TripStatus.valueOf(fields[1].strip().toUpperCase(Locale.ROOT)); }
                            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Неизвестный статус"); }
                            for (BusinessTrip trip : service.findByStatus(status)) printTrip(trip);
                            break;
                        case "exit": requireFields(fields, 1); return;
                        case "submit": case "approve": case "cancel": case "show":
                            requireFields(fields, 1); requireTrip(current);
                            if (command.equals("submit")) current.submit();
                            else if (command.equals("approve")) current.approve();
                            else if (command.equals("cancel")) current.cancel();
                            printTrip(current); break;
                        case "reject":
                            requireFields(fields, 2); requireTrip(current);
                            current.reject(fields[1]); printTrip(current); break;
                        case "complete":
                            if (fields.length != 1 && fields.length != 2)
                                throw new IllegalArgumentException("Нужно 1 или 2 поля");
                            requireTrip(current);
                            LocalDate today = fields.length == 2 ? parseDate(fields[1])
                                    : LocalDate.now(ZoneId.of("Europe/Moscow"));
                            if (fields.length == 2) System.out.println("Тестовая дата: " + today);
                            current.complete(today); printTrip(current); break;
                        default: throw new IllegalArgumentException("Неизвестная команда");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }
    private static LocalDate parseDate(String value) {
        String text = value.strip();
        if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
            throw new IllegalArgumentException("Дата должна иметь формат ГГГГ-ММ-ДД");
        try { return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Несуществующая календарная дата"); }
    }
    private static void requireFields(String[] fields, int count) {
        if (fields.length != count) throw new IllegalArgumentException("Нужно полей: " + count);
    }
    private static void requireTrip(BusinessTrip trip) {
        if (trip == null) throw new IllegalStateException("Сначала создайте или выберите заявку");
    }
    private static void printTrip(BusinessTrip trip) {
        System.out.println(trip.getId() + " " + trip.getStart() + ".." + trip.getEnd() + " "
                + trip.getStatus() + (trip.getRejectionReason() == null ? "" : " | " + trip.getRejectionReason()));
    }
}
