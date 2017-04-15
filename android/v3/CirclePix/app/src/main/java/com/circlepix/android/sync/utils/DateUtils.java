package com.circlepix.android.sync.utils;

import com.circlepix.android.sync.commands.ErrorHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sburns.
 */
public class DateUtils {
    public static final long DATEDIFF_ERROR_RESULT = -1;
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    /**
     * Format API date.
     * @param date the date to format (or null if you want to use current date/time)
     * @return the date.
     */
    public static String formatDateForRestAPI (final Date date) {

        // use current time if none supplied
        return API_DATE_FORMAT.format(date == null ? new Date() : date);
    }

    /**
     * Return the number of milliseconds between two dates
     * @param date1 first date
     * @param date2 second date
     * @return 0 means dates are equal
     *           positive value means date2 is greater than date1
     *           negative value means date1 is greater than date2
     */
    public static long getMilliSecondsDifference (final Date date1, final Date date2) {

        return date1.getTime() > date2.getTime() ?
                -(date1.getTime() - date2.getTime()) :
                date2.getTime() - date1.getTime();
    }

    /**
     * Return the number of milliseconds between two dates
     * @param dateString1 first date
     * @param dateString2 second date
     * @return 0 means dates are equal
     *           positive value means dateString2 is greater than dateString1
     *           negative value means dateString1 is greater than dateString2
     *           -1 value means there was a parsing error
     */
    public static long getMilliSecondsDifference (final String dateString1, final String dateString2) {

        try {
            return getMilliSecondsDifference(
                    parseAPIFormattedDateString(dateString1),
                    parseAPIFormattedDateString(dateString2));
        } catch(ParseException e) {
            ErrorHandler.log("Date parse error: ", e.toString());
        }
        return DATEDIFF_ERROR_RESULT;
    }

    /**
     * Parse a date string of the API format we expect
     * @param dateString date string
     * @return date
     * @throws ParseException
     */
    public static Date parseAPIFormattedDateString (final String dateString) throws ParseException {

        if (dateString == null || dateString.isEmpty()) {
            throw new ParseException("Null or empty date passed to DateUtil.parseAPIFormattedDateString()", 0);
        }

        return API_DATE_FORMAT.parse(dateString);
    }

}
