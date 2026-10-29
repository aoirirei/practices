package ru.edu.pr04;

import java.util.List;

public class SelfCheck {
    private static int passed;
    private static int failed;
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
        throw new AssertionError("Ожидалось исключение " + type.getSimpleName());
    }
    private static Order order() { return new OrderService().createOrder(new Employee("E1", "Test User")); }
    public static void main(String[] args) {
        Product p = new Product("P1", "Mouse", 1000);
        test("original product", () -> check(p.getPrice() == 1000));
        test("blank product id", () -> expect(IllegalArgumentException.class, () -> new Product(" ", "Mouse", 1)));
        test("null name", () -> expect(IllegalArgumentException.class, () -> new Product("P", null, 1)));
        test("zero price", () -> expect(IllegalArgumentException.class, () -> new Product("P", "M", 0)));
        test("negative price", () -> expect(IllegalArgumentException.class, () -> new Product("P", "M", -1)));
        test("NaN price", () -> expect(IllegalArgumentException.class, () -> new Product("P", "M", Double.NaN)));
        test("infinite price", () -> expect(IllegalArgumentException.class, () -> new Product("P", "M", Double.POSITIVE_INFINITY)));
        test("blank employee", () -> expect(IllegalArgumentException.class, () -> new Employee("E", " ")));
        test("null employee", () -> expect(IllegalArgumentException.class, () -> new Order(null)));
        test("quantity 1", () -> check(new OrderItem(p, 1).getLineTotal() == 1000));
        test("quantity 10", () -> check(new OrderItem(p, 10).getLineTotal() == 10000));
        test("quantity 0", () -> expect(IllegalArgumentException.class, () -> new OrderItem(p, 0)));
        test("quantity 11", () -> expect(IllegalArgumentException.class, () -> new OrderItem(p, 11)));
        test("null product", () -> expect(IllegalArgumentException.class, () -> order().addProduct(null, 1)));
        test("negative delta", () -> expect(IllegalArgumentException.class, () -> new OrderItem(p, 1).increase(-1)));
        test("zero delta", () -> expect(IllegalArgumentException.class, () -> new OrderItem(p, 1).increase(0)));
        test("huge delta", () -> expect(IllegalArgumentException.class, () -> new OrderItem(p, 1).increase(Integer.MAX_VALUE)));
        test("immutable item", () -> { OrderItem a = new OrderItem(p, 6); OrderItem b = a.increase(4); check(a.getQuantity() == 6 && b.getQuantity() == 10); });
        test("merge 6 plus 4", () -> { Order o = order(); o.addProduct(p, 6); o.addProduct(p, 4); check(o.getItems().size() == 1 && o.getItems().get(0).getQuantity() == 10 && o.total() == 10000); });
        test("merge 6 plus 5 rollback", () -> { Order o = order(); o.addProduct(p, 6); expect(IllegalArgumentException.class, () -> o.addProduct(p, 5)); check(o.total() == 6000 && o.getItems().get(0).getQuantity() == 6); });
        test("same id same data", () -> { Order o = order(); o.addProduct(p, 1); o.addProduct(new Product("P1", "Mouse", 1000), 1); check(o.getItems().size() == 1 && o.total() == 2000); });
        test("same id conflict", () -> { Order o = order(); o.addProduct(p, 1); expect(IllegalArgumentException.class, () -> o.addProduct(new Product("P1", "Other", 5), 1)); check(o.total() == 1000); });
        test("two products total", () -> { Order o = order(); o.addProduct(p, 2); o.addProduct(new Product("K", "Keyboard", 4490), 3); check(o.total() == 15470); });
        test("remove recalculates", () -> { Order o = order(); o.addProduct(p, 2); o.removeProduct("P1"); check(o.total() == 0 && o.getItems().isEmpty()); });
        test("unknown remove no-op", () -> { Order o = order(); o.addProduct(p, 1); o.removeProduct("unknown"); check(o.total() == 1000); });
        test("blank remove", () -> expect(IllegalArgumentException.class, () -> order().removeProduct(" ")));
        test("empty total", () -> check(order().total() == 0));
        test("confirm changes status", () -> { Order o = order(); o.addProduct(p, 1); o.confirm(); check(o.getStatus() == OrderStatus.CONFIRMED); });
        test("confirmed add blocked", () -> { Order o = order(); o.confirm(); expect(IllegalStateException.class, () -> o.addProduct(p, 1)); check(o.total() == 0); });
        test("confirmed remove blocked", () -> { Order o = order(); o.addProduct(p, 1); o.confirm(); expect(IllegalStateException.class, () -> o.removeProduct("P1")); check(o.total() == 1000); });
        test("repeat confirm blocked", () -> { Order o = order(); o.confirm(); expect(IllegalStateException.class, o::confirm); });
        test("list cannot clear", () -> { Order o = order(); o.addProduct(p, 1); expect(UnsupportedOperationException.class, () -> o.getItems().clear()); check(o.total() == 1000); });
        test("item cannot mutate order", () -> { Order o = order(); o.addProduct(p, 1); OrderItem a = o.getItems().get(0); o.confirm(); a.increase(1); check(o.total() == 1000 && o.getItems().get(0).getQuantity() == 1); });
        test("list snapshot", () -> { Order o = order(); o.addProduct(p, 1); List<OrderItem> snapshot = o.getItems(); o.addProduct(p, 1); check(snapshot.get(0).getQuantity() == 1 && o.total() == 2000); });
        test("catalog canonical product", () -> { OrderService s = new OrderService(); check(s.createProduct("P", "M", 1) == s.createProduct("P", "M", 1)); });
        test("catalog conflict", () -> { OrderService s = new OrderService(); s.createProduct("P", "M", 1); expect(IllegalArgumentException.class, () -> s.createProduct("P", "M", 2)); });
        test("line overflow", () -> expect(IllegalArgumentException.class, () -> new OrderItem(new Product("H", "Huge", Double.MAX_VALUE), 2)));
        test("order overflow rollback", () -> { Order o = order(); o.addProduct(new Product("H1", "Huge1", Double.MAX_VALUE), 1); expect(IllegalArgumentException.class, () -> o.addProduct(new Product("H2", "Huge2", Double.MAX_VALUE), 1)); check(o.getItems().size() == 1); });
        test("sample valid rows", () -> { Order o = order(); Product m = new Product("M01", "Monitor 27", 32990); o.addProduct(m, 2); o.addProduct(new Product("K01", "Keyboard", 4490), 3); o.addProduct(m, 1); check(o.getItems().size() == 2 && o.total() == 112440); });
        test("employee access", () -> check(order().getEmployee().id().equals("E1")));
        System.out.println("Passed: " + passed + "; failed: " + failed);
        if (failed > 0) throw new AssertionError("SelfCheck failed");
    }
}
