package ru.edu.pr05;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BusinessTripService {
    private final Map<String, BusinessTrip> trips = new LinkedHashMap<>();
    public BusinessTrip create(String id, LocalDate start, LocalDate end) {
        BusinessTrip trip = new BusinessTrip(id, start, end);
        if (trips.containsKey(trip.getId()))
            throw new IllegalArgumentException("Заявка с таким id уже существует");
        trips.put(trip.getId(), trip);
        return trip;
    }
    public BusinessTrip getById(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Нужен id заявки");
        BusinessTrip trip = trips.get(id.strip());
        if (trip == null) throw new IllegalArgumentException("Заявка не найдена");
        return trip;
    }
    public List<BusinessTrip> findByStatus(TripStatus status) {
        if (status == null) throw new IllegalArgumentException("Статус поиска не задан");
        List<BusinessTrip> result = new ArrayList<>();
        for (BusinessTrip trip : trips.values()) {
            if (trip.getStatus() == status) result.add(trip);
        }
        return List.copyOf(result);
    }
}
