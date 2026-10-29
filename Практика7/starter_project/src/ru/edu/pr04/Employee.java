package ru.edu.pr04;

public record Employee(String id, String fullName) {
    public Employee {
        if (id == null || id.isBlank() || fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Нужны id и ФИО сотрудника");
        id = id.trim(); fullName = fullName.trim();
    }
}
