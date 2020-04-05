package com.example.bookly;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateContract {

    public final static String PAY_DATE_FORMAT = "yyyy/MM/dd";

    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat(DateContract.PAY_DATE_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }
}
