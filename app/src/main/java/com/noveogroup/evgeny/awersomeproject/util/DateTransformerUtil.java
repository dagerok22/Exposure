package com.noveogroup.evgeny.awersomeproject.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransformerUtil {
    public static final String DATE_FORMAT = "yyyy.MM.dd G 'at' HH:mm:ss z";

    public static String getDateAsString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date parsedDate;
        try {
            parsedDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            parsedDate = new Date();
        }
        return parsedDate;
    }
}
