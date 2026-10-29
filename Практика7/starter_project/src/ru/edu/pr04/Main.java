package ru.edu.pr04;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        OrderService service = new OrderService();
        Order order = service.createOrder(new Employee("E01", "Еремеева"));
        System.out.println("add;id;name;price;quantity | remove;id | list | total | confirm | exit");
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            while (scanner.hasNextLine()) {
                String[] fields = scanner.nextLine().split(";", -1);
                String command = fields[0].trim().toLowerCase(Locale.ROOT);
                try {
                    switch (command) {
                        case "add":
                            requireFields(fields, 5);
                            // При подтверждённом заказе каталог тоже не меняется.
                            if (order.getStatus() != OrderStatus.DRAFT)
                                throw new IllegalStateException("Подтверждённый заказ нельзя изменять");
                            double price = Double.parseDouble(fields[3].trim());
                            int quantity = Integer.parseInt(fields[4].trim());
                            Product product = service.createProduct(fields[1], fields[2], price);
                            order.addProduct(product, quantity);
                            System.out.println("Добавлено"); break;
                        case "remove":
                            requireFields(fields, 2); order.removeProduct(fields[1]);
                            System.out.println("Удаление выполнено"); break;
                        case "total":
                            requireFields(fields, 1);
                            System.out.printf(Locale.ROOT, "Итого: %.2f%n", order.total()); break;
                        case "list":
                            requireFields(fields, 1);
                            for (OrderItem item : order.getItems())
                                System.out.printf(Locale.ROOT, "%s %s x%d = %.2f%n",
                                        item.getProduct().getId(), item.getProduct().getName(),
                                        item.getQuantity(), item.getLineTotal());
                            System.out.println("Статус: " + order.getStatus()); break;
                        case "confirm":
                            requireFields(fields, 1); order.confirm();
                            System.out.println("Заказ подтверждён"); break;
                        case "exit": requireFields(fields, 1); return;
                        default: throw new IllegalArgumentException("Неизвестная команда");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: неверный формат цены или количества");
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }
    private static void requireFields(String[] fields, int count) {
        if (fields.length != count)
            throw new IllegalArgumentException("Нужно полей: " + count);
    }
}
