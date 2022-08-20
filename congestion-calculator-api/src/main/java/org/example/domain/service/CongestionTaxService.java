package org.example.domain.service;

import org.example.domain.model.SlotPrice;
import org.example.domain.model.Vehicle;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class CongestionTaxService {
    private static final Set<String> TOLL_FREE_VEHICLES = Set.of(
            "Motorcycle",
            "Bus",
            "Emergency",
            "Diplomat",
            "Foreign",
            "Military");

    private static final HashSet<Date> PUBLIC_HOLIDAY = new HashSet<>(List.of(
            new Date(2013 - 1900, Calendar.JANUARY, 1),
            new Date(2013 - 1900, Calendar.MARCH, 28),
            new Date(2013 - 1900, Calendar.MARCH, 29),
            new Date(2013 - 1900, Calendar.APRIL, 1),
            new Date(2013 - 1900, Calendar.APRIL, 30),
            new Date(2013 - 1900, Calendar.MAY, 1),
            new Date(2013 - 1900, Calendar.MAY, 8),
            new Date(2013 - 1900, Calendar.MAY, 9),
            new Date(2013 - 1900, Calendar.JUNE, 5),
            new Date(2013 - 1900, Calendar.JUNE, 6),
            new Date(2013 - 1900, Calendar.JUNE, 21),
            new Date(2013 - 1900, Calendar.NOVEMBER, 1),
            new Date(2013 - 1900, Calendar.DECEMBER, 24),
            new Date(2013 - 1900, Calendar.DECEMBER, 25),
            new Date(2013 - 1900, Calendar.DECEMBER, 26),
            new Date(2013 - 1900, Calendar.DECEMBER, 31)
    ));

    private static final HashSet<SlotPrice> SLOT_PRICES = new HashSet<>(List.of(
            new SlotPrice(6, 0, 6, 29, 8),
            new SlotPrice(6, 30, 6, 59, 13),
            new SlotPrice(7, 0, 7, 59, 18),
            new SlotPrice(8, 0, 8, 29, 13),
            new SlotPrice(8, 30, 14, 59, 8),
            new SlotPrice(15, 0, 15, 29, 13),
            new SlotPrice(15, 30, 16, 59, 18),
            new SlotPrice(17, 0, 17, 59, 13),
            new SlotPrice(18, 0, 18, 29, 8)
    ));

    public int getTax(Vehicle vehicle, Date[] dates) {
        Date intervalStart = dates[0];
        int totalFee = 0;

        for (Date date : dates) {
            int year = date.getYear() + 1900;
            if (year != 2013) throw new IllegalArgumentException("Year " + year + " is not yet supported !");
            int nextFee = getTollFee(date, vehicle);
            int tempFee = getTollFee(intervalStart, vehicle);

            long diffInMillis = date.getTime() - intervalStart.getTime();
            long minutes = diffInMillis / 1000 / 60;

            if (minutes <= 60) {
                if (totalFee > 0) totalFee -= tempFee;
                totalFee += Math.max(nextFee, tempFee);
            } else {
                totalFee += nextFee;
            }
        }
        return Math.min(60, totalFee);
    }

    private boolean isTollFreeVehicle(Vehicle vehicle) {
        if (vehicle == null) return false;
        String vehicleType = vehicle.getVehicleType();
        return TOLL_FREE_VEHICLES.contains(vehicleType);
    }

    public int getTollFee(Date date, Vehicle vehicle) {
        if (isTollFreeDate(date) || isTollFreeVehicle(vehicle)) return 0;

        int hour = date.getHours();
        int minute = date.getMinutes();

        for (SlotPrice slotPrice : SLOT_PRICES) {
            if (slotPrice.contains(hour, minute))
                return slotPrice.price();
        }
        return 0;
    }

    private boolean isTollFreeDate(Date date) {
        int month = date.getMonth();
        int day = date.getDay() + 1;
        Date dayDate = new Date(date.getYear(), date.getMonth(), date.getDate());
        Date dayDateAfter = nextDay(dayDate);
        return isFreeDay(day) || isFreeMonth(month) || PUBLIC_HOLIDAY.contains(dayDate) || PUBLIC_HOLIDAY.contains(dayDateAfter);
    }

    private Date nextDay(Date dayDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dayDate);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    private boolean isFreeMonth(int month) {
        return month == Calendar.JULY;
    }

    private boolean isFreeDay(int day) {
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
    }
}
