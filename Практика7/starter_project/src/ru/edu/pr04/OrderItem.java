package ru.edu.pr04;

/** Неизменяемая позиция; увеличение создаёт новую позицию. */
public final class OrderItem {
    public static final int MAX_QUANTITY = 10;
    private final Product product;
    private final int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Товар не задан");
        if (quantity < 1 || quantity > MAX_QUANTITY)
            throw new IllegalArgumentException("Количество должно быть от 1 до " + MAX_QUANTITY);
        if (!Double.isFinite(product.getPrice() * quantity))
            throw new IllegalArgumentException("Стоимость позиции слишком велика");
        this.product = product; this.quantity = quantity;
    }
    public OrderItem increase(int delta) {
        if (delta < 1 || delta > MAX_QUANTITY - quantity)
            throw new IllegalArgumentException("Превышен лимит или неверное увеличение количества");
        return new OrderItem(product, quantity + delta);
    }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return product.getPrice() * quantity; }
}
