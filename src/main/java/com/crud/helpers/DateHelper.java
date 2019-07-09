package com.crud.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.*;

public class DateHelper {

    public static DateTimeFormatter defaultFormat = ofPattern("yyyy-MM-dd").withLocale(Locale.ENGLISH);
    public static DateTimeFormatter inputFormat = ofPattern("yyyy-M-d").withLocale(Locale.ENGLISH);
    public static DateTimeFormatter tableFormat = ofPattern("dd MMM yyyy").withLocale(Locale.ENGLISH);

    public static String convertToDefaultFormat(String date, DateTimeFormatter formatOfDate) {
        return LocalDate.parse(date, formatOfDate).format(defaultFormat);
    }
}
