package com.union.base.util;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil extends cn.hutool.core.date.DateUtil {


    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    public static String formatDate(long dateTime, String pattern) {
        return format(new Date(dateTime), pattern);
    }

    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        return parse(str.toString());
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }
}
