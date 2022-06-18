package com.amazon.grayjayreportvalidationautomation.utils;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CalendarUtil {
    AppConfigUtil appConfigUtil;
    public static final Logger LOGGER = LogManager.getLogger(CalendarUtil.class);

    @Inject
    public  CalendarUtil(AppConfigUtil appConfigUtil) {
        this.appConfigUtil = appConfigUtil;
    }

    public Date convertStringToDate(String date) throws ParseException {
        Locale locale = Locale.ROOT;
        TimeZone timeZone = TimeZone.getTimeZone(appConfigUtil.getTimeZone());
        Calendar cal = Calendar.getInstance(timeZone,locale);
        DateFormat df = new SimpleDateFormat(appConfigUtil.getDatePattern(),locale);
        df.setTimeZone(TimeZone.getTimeZone(appConfigUtil.getTimeZone()));
        cal.setTime(df.parse(date));
        return cal.getTime();

    }
}
