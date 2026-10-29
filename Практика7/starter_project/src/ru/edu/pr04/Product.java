package ru.edu.pr04;

public final class Product {
    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        if (id == null || id.isBlank() || name == null || name.isBlank())
            throw new IllegalArgumentException("Нужны id и наименование товара");
        if (!Double.isFinite(price) || price <= 0)
            throw new IllegalArgumentException("Цена должна быть положительной и конечной");
        this.id = id.trim(); this.name = name.trim(); this.price = price;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean sameDefinition(Product other) {
        return other != null && id.equals(other.id) && name.equals(other.name)
                && Double.compare(price, other.price) == 0;
    }
}
