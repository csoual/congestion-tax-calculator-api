package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CongestionTaxCalculatorTest {

    private static Stream<Arguments> taxCalculator() {
        return Stream.of(
                Arguments.of("Saturday", 0, new Date(2013 - 1900, Calendar.JANUARY, 5, 6, 0, 0)),
                Arguments.of("Sunday", 0, new Date(2013 - 1900, Calendar.JANUARY, 6, 6, 0, 0)),
                Arguments.of("July", 0, new Date(2013 - 1900, Calendar.JULY, 1, 6, 0, 0)),
                Arguments.of("Public holiday", 0, new Date(2013 - 1900, Calendar.JANUARY, 1, 6, 0, 0)),
                Arguments.of("Public holiday", 0, new Date(2013 - 1900, Calendar.JANUARY, 6, 6, 0, 0)),
                Arguments.of("Public holiday", 0, new Date(2013 - 1900, Calendar.APRIL, 30, 6, 0, 0)),
                Arguments.of("Day Before Public holiday", 0, new Date(2013 - 1900, Calendar.APRIL, 29, 6, 0, 0)),
                Arguments.of("Public holiday", 0, new Date(2013 - 1900, Calendar.JUNE, 6, 6, 0, 0)),
                Arguments.of("Day Before Public holiday", 0, new Date(2013 - 1900, Calendar.JUNE, 5, 6, 0, 0)),
                Arguments.of("Public holiday", 0, new Date(2013 - 1900, Calendar.DECEMBER, 24, 6, 0, 0)),
                Arguments.of("Day Before Public holiday", 0, new Date(2013 - 1900, Calendar.DECEMBER, 23, 6, 0, 0)),
                Arguments.of("Slot", 8, new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 0, 0)),
                Arguments.of("Slot", 13, new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 30, 0)),
                Arguments.of("Slot", 18, new Date(2013 - 1900, Calendar.JANUARY, 7, 7, 0, 0)),
                Arguments.of("Slot", 13, new Date(2013 - 1900, Calendar.JANUARY, 7, 8, 0, 0)),
                Arguments.of("Slot", 8, new Date(2013 - 1900, Calendar.JANUARY, 7, 8, 30, 0)),
                Arguments.of("Slot", 13, new Date(2013 - 1900, Calendar.JANUARY, 7, 15, 0, 0)),
                Arguments.of("Slot", 18, new Date(2013 - 1900, Calendar.JANUARY, 7, 15, 30, 0)),
                Arguments.of("Slot", 13, new Date(2013 - 1900, Calendar.JANUARY, 7, 17, 0, 0)),
                Arguments.of("Slot", 8, new Date(2013 - 1900, Calendar.JANUARY, 7, 18, 0, 0)),
                Arguments.of("Slot", 0, new Date(2013 - 1900, Calendar.JANUARY, 7, 18, 30, 0))
        );
    }

    @ParameterizedTest
    @MethodSource
    void taxCalculator(String display, int excepted, Date input) {
        int tax = new CongestionTaxCalculator().getTax(() -> "Car", new Date[]{input});
        Assertions.assertEquals(excepted, tax);
    }

    private static Stream<Arguments> taxCalculatorMultipleDates() {
        return Stream.of(
                Arguments.of("Two passes within 60 minutes", 13, new Date[]{
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 0, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 30, 0)
                }),
                Arguments.of("Two passes within 120 minutes", 26, new Date[]{
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 0, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 7, 30, 0)
                }),
                Arguments.of("Multiple passes within one day", 60, new Date[]{
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 7, 0, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 8, 15, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 15, 30, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 17, 30, 0),
                        new Date(2013 - 1900, Calendar.JANUARY, 7, 18, 15, 0)
                })
        );
    }

    @ParameterizedTest
    @MethodSource
    void taxCalculatorMultipleDates(String display, int excepted, Date[] input) {
        int tax = new CongestionTaxCalculator().getTax(() -> "Car", input);
        Assertions.assertEquals(excepted, tax);
    }

    private static Stream<Arguments> taxCalculatorVehiclesType() {
        return Stream.of(
                Arguments.of("Emergency", 0),
                Arguments.of("Bus", 0),
                Arguments.of("Diplomat", 0),
                Arguments.of("Motorcycle", 0),
                Arguments.of("Military", 0),
                Arguments.of("Foreign", 0),
                Arguments.of("Tractor", 8)
        );
    }

    @ParameterizedTest
    @MethodSource
    void taxCalculatorVehiclesType(String vehicleType, int excepted) {
        int tax = new CongestionTaxCalculator().getTax(() -> vehicleType, new Date[]{new Date(2013 - 1900, Calendar.JANUARY, 7, 6, 0, 0)});
        Assertions.assertEquals(excepted, tax);
    }

}