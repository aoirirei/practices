package ru.edu.pr04;

import java.util.HashMap;
import java.util.Map;

/** Каталог гарантирует единственное описание товара для каждого id в приложении. */
public class OrderService {
    private final Map<String, Product> catalog = new HashMap<>();
    public Order createOrder(Employee employee) { return new Order(employee); }
    public Product createProduct(String id, String name, double price) {
        Product product = new Product(id, name, price);
        Product existing = catalog.get(product.getId());
        if (existing != null) {
            if (!existing.sameDefinition(product))
                throw new IllegalArgumentException("В каталоге уже есть другой товар с id " + product.getId());
            return existing;
        }
        catalog.put(product.getId(), product);
        return product;
    }
}
