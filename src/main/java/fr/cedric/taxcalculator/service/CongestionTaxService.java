package fr.cedric.taxcalculator.service;

import fr.cedric.taxcalculator.config.CongestionTaxConfig;
import fr.cedric.taxcalculator.service.model.Vehicle;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
@AllArgsConstructor
public class CongestionTaxService {

    CongestionTaxConfig congestionTaxConfig;

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
        return congestionTaxConfig.tollFreeVehicles().contains(vehicleType);
    }

    public int getTollFee(Date date, Vehicle vehicle) {
        if (isTollFreeDate(date) || isTollFreeVehicle(vehicle)) return 0;

        int hour = date.getHours();
        int minute = date.getMinutes();

        for (CongestionTaxConfig.SlotPrice slotPrice : congestionTaxConfig.slotPrices()) {
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
