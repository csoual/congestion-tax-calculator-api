package org.example.domain.model;

public record SlotPrice(int startHour, int startMinute, int endHour, int endMinute, int price) {
    public boolean contains(int hour, int minute) {
        return (hour >= startHour() && minute >= startMinute()) &&
                (hour <= endHour() && minute <= endMinute());
    }
}
