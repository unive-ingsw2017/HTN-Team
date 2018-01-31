package com.greenteadev.unive.clair.util;

import android.support.v4.util.Pair;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;


/**
 * Created by Hitech95 on 22/01/2018.
 */

public class DateUtils {

    public static LocalDate getMonday(LocalDate dayPivot) {
        return dayPivot.withDayOfWeek(DateTimeConstants.MONDAY);
    }

    public static LocalDate getFirstMonth(LocalDate dayPivot) {
        return dayPivot.withDayOfMonth(1);
    }

    public static LocalDate getLastWeek(LocalDate dayPivot) {
        return dayPivot.minusDays(7);
    }

    public static LocalDate getLast7Day(LocalDate dayPivot) {
        return dayPivot.minusDays(6);
    }

    public static LocalDate getLastMonth(LocalDate dayPivot) {
        return dayPivot.minusMonths(1);
    }

    public static Pair<LocalDate, LocalDate> getWeekStartEnd(LocalDate target) {

        LocalDate weekStart = target.dayOfWeek().withMinimumValue();
        LocalDate weekEnd = target.dayOfWeek().withMaximumValue();

        return new Pair(weekStart, weekEnd);
    }

    public static Pair<LocalDate, LocalDate> getMonthStartEnd(LocalDate target) {

        LocalDate monthStart = target.dayOfMonth().withMinimumValue();
        LocalDate monthEnd = target.dayOfMonth().withMaximumValue();

        return new Pair(monthStart, monthEnd);
    }

    public static Pair<LocalDate, LocalDate> getYearStartEnd(LocalDate target) {

        LocalDate yearStart = target.dayOfYear().withMinimumValue();

        LocalDate yearEnd = target.dayOfYear().withMaximumValue();

        return new Pair(yearStart, yearEnd);
    }
}
