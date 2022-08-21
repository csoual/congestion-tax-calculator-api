package org.example.config;

import io.smallrye.config.ConfigMapping;

import java.util.Set;

@ConfigMapping(prefix = "config")
public interface CongestionTaxConfig {

    Set<String> tollFreeVehicles();
    Set<SlotPrice> slotPrices();

    interface SlotPrice {
        Integer startHour();

        Integer startMinute();

        Integer endHour();

        Integer endMinute();

        Integer price();

        default boolean contains(int hour, int minute) {
            return (hour >= startHour() && minute >= startMinute()) &&
                    (hour <= endHour() && minute <= endMinute());
        }

    }
}
