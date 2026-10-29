package ru.edu.pr04;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        if (employee == null) throw new IllegalArgumentException("Сотрудник не задан");
        this.employee = employee;
    }
    private void ensureDraft() {
        if (status != OrderStatus.DRAFT)
            throw new IllegalStateException("Подтверждённый заказ нельзя изменять");
    }
    public void addProduct(Product product, int quantity) {
        ensureDraft();
        OrderItem candidate = new OrderItem(product, quantity);
        for (int i = 0; i < items.size(); i++) {
            OrderItem existing = items.get(i);
            if (existing.getProduct().getId().equals(product.getId())) {
                if (!existing.getProduct().sameDefinition(product))
                    throw new IllegalArgumentException("Конфликт данных товара с id " + product.getId());
                candidate = existing.increase(quantity);
                if (!Double.isFinite(total() - existing.getLineTotal() + candidate.getLineTotal()))
                    throw new IllegalArgumentException("Сумма заказа слишком велика");
                items.set(i, candidate);
                return;
            }
        }
        if (!Double.isFinite(total() + candidate.getLineTotal()))
            throw new IllegalArgumentException("Сумма заказа слишком велика");
        items.add(candidate);
    }
    public void removeProduct(String productId) {
        ensureDraft();
        if (productId == null || productId.isBlank())
            throw new IllegalArgumentException("Нужен id товара");
        String id = productId.trim();
        items.removeIf(item -> item.getProduct().getId().equals(id));
    }
    public double total() {
        double sum = 0;
        for (OrderItem item : items) sum += item.getLineTotal();
        return sum;
    }
    public void confirm() {
        ensureDraft();
        status = OrderStatus.CONFIRMED;
    }
    public List<OrderItem> getItems() { return List.copyOf(items); }
    public OrderStatus getStatus() { return status; }
    public Employee getEmployee() { return employee; }
}
