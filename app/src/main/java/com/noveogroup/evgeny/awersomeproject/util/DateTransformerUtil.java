package com.noveogroup.evgeny.awersomeproject.util;

import android.content.Context;

import com.noveogroup.evgeny.awersomeproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransformerUtil {
    private static final String DATE_FORMAT = "yyyy.MM.dd G 'at' HH:mm:ss z";
    private static SimpleDateFormat simpleDateFormat;

    public static String getDateAsString(Date date) {
        if(simpleDateFormat == null){
             simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        }
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String dateString) {
        if(simpleDateFormat == null){
            simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        }
        Date parsedDate;
        try {
            parsedDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            parsedDate = new Date();
        }
        return parsedDate;
    }

    public static String getAgeOfTask(String dateString, Context context) {
        Date date = getDateFromString(dateString);
        long different = (new Date()).getTime() - date.getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;

        StringBuilder stringBuilder = new StringBuilder();
        if (elapsedDays > 0) {
            stringBuilder
                    .append(elapsedDays)
                    .append(" day");
            if (elapsedDays > 1) {
                stringBuilder.append("s");
            }
        }
        if (elapsedDays > 0 && elapsedHours > 0) {
            stringBuilder.append(", ");
        }
        if (elapsedHours > 0) {
            stringBuilder.append(elapsedHours)
                    .append(" hour");
            if (elapsedHours > 1) {
                stringBuilder.append("s");
            }
        }
        if (elapsedDays < 1 && elapsedHours < 1) {
            stringBuilder.append(context.getResources().getString(R.string.less_then_hour_task_age));

        }
        return stringBuilder.toString();
    }
}
