package com.jacob.backend.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Utils {

    public static Double baseDays = 360.0;

    public static long getMonths(LocalDate initDate, LocalDate finishDate) {
        long months = ChronoUnit.MONTHS.between(initDate, finishDate);
        return months > 0 ? months : 1;
    }

    public static long getDays(LocalDate initDate, LocalDate finishDate) {
        return ChronoUnit.DAYS.between(initDate, finishDate);
    }
}
