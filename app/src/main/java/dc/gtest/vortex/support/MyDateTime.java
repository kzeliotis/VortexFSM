package dc.gtest.vortex.support;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyDateTime {

    private static final SimpleDateFormat format_MM_dd_yyyy_HH_mm = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    private static final SimpleDateFormat serverShortDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat appDateTimeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat appDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat appTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public static String get_MM_dd_yyyy_HH_mm_from_now() {

        Calendar calendar = Calendar.getInstance();

        try {
            Date date = calendar.getTime();
            return format_MM_dd_yyyy_HH_mm.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getServerFormatFormAppDateTime(String dateString) {

        try {
            Date date = appDateTimeFormat.parse(dateString);
            return serverShortDateTimeFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_app_date_from_day_month_year_integers(int day, int month, int year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Date date = calendar.getTime();

        try {
            return appDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_app_time_from_hour_minute_integers(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Date date = calendar.getTime();

        try {
            return appTimeFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Date getDateFromAssignmentDate(String date) {
        try {
            return format_MM_dd_yyyy_HH_mm.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String get_server_short_format_current_date_time() {
        Calendar calendar = Calendar.getInstance();
        long currentUtcMillis = calendar.getTimeInMillis();
        return serverShortDateTimeFormat.format(currentUtcMillis);
    }

    public static String getCurrentTime() {
        Calendar date = Calendar.getInstance();
        long currentUtcMillis = date.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
        return sdf.format(currentUtcMillis);
    }

    public static String getCurrentTimeWithSeconds() {
        Calendar date = Calendar.getInstance();
        long currentUtcMillis = date.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        return sdf.format(currentUtcMillis);
    }

    public static String getUtc() {
        serverDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return serverDateFormat.format(new Date());
    }

    public static String getLocal(String utcDateTime) {

        serverDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date myDate = serverDateFormat.parse(utcDateTime);
            return serverDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static Date getDateFromString(String dateString) {

        try {
            return serverDateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getWeekDateMonth(Date date) {

        try {
            SimpleDateFormat targetFormat = new SimpleDateFormat("EEEE dd MMMM", Locale.ENGLISH);
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_app_format_today() {
        Date date = Calendar.getInstance().getTime();
        return appDateFormat.format(date);
    }

    public static String get_server_format_today() {
        Date date = Calendar.getInstance().getTime();
        return serverDateFormat.format(date);
    }

    public static String get_current_week_yyyy_week_of_year() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        return year + "-" + weekOfYear;
    }

    public static String get_current_month_yyyy_MM() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        return targetFormat.format(date);
    }

    public static String get_server_format_start_this_week(int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        Date date = calendar.getTime();
        return serverDateFormat.format(date);
    }

    public static String get_server_format_start_this_month() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        return serverDateFormat.format(date);
    }

    public static String get_server_format_start_this_year() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        return serverDateFormat.format(date);
    }

    public static String get_last_month_yyyy_MM() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        Date date = calendar.getTime();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        return targetFormat.format(date);
    }

    public static String getToday_plus_minus_N_days_dd_MMM_yyyy(int daysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + daysOffset);
        Date date = calendar.getTime();
        return appDateFormat.format(date);
    }

    public static long get_millis_from_date_string(String dateString) {
        try {
            Date date = serverDateFormat.parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return get_today_millis();
    }

    public static long get_today_millis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public static long getToday_plus_minus_N_days_millis(int daysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + daysOffset);
        return calendar.getTimeInMillis();
    }

//    public static long get_7_days_ago_millis() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 7);
//        return calendar.getTimeInMillis();
//    }

//    public static long get_30_days_ago_millis() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 30);
//        return calendar.getTimeInMillis();
//    }

    public static int getThisMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public static int getLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        return calendar.get(Calendar.MONTH);
    }

    public static String get_current_quarter_yyyy_q() {
        Calendar calendar = Calendar.getInstance();
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        switch (mMonth) {
            case 0:
            case 1:
            case 2:
                return mYear + "-" + 0;
            case 3:
            case 4:
            case 5:
                return mYear + "-" + 1;
            case 6:
            case 7:
            case 8:
                return mYear + "-" + 2;
            case 9:
            case 10:
            case 11:
                return mYear + "-" + 3;
        }

        return "";
    }

    public static String get_last_quarter_yyyy_q() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 3);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        switch (mMonth) {
            case 0:
            case 1:
            case 2:
                return mYear + "-" + 0;
            case 3:
            case 4:
            case 5:
                return mYear + "-" + 1;
            case 6:
            case 7:
            case 8:
                return mYear + "-" + 2;
            case 9:
            case 10:
            case 11:
                return mYear + "-" + 3;
        }

        return "";
    }

    public static int getThisYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public static String getServerDateFromAppDate(String dateString) {
        try {
            Date date = appDateFormat.parse(dateString);
            return serverDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String getAppDateFromDate(Date date) {
        return appDateFormat.format(date);
    }

    public static String getServerDateFromAssignmentDate(String dateString) {
        try {
            Date date = format_MM_dd_yyyy_HH_mm.parse(dateString);
            return serverDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String getAppDateFromAssignmentDate(String dateString) {
        try {
            Date date = format_MM_dd_yyyy_HH_mm.parse(dateString);
            return appDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_dd_MMM_yyyy(Date date) {
        try {
            return appDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_dd_MMM_yyyy(String dateString) {
        try {
            Date date = serverDateFormat.parse(dateString);
            return appDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int get_day(String dateString) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(appDateFormat.parse(dateString));
            return calendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int get_month(String dateString) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(appDateFormat.parse(dateString));
            return calendar.get(Calendar.MONTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar.get(Calendar.MONTH);
    }

    public static String get_quarter_yyyy_q(String dateString) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(serverDateFormat.parse(dateString));

            int mMonth = calendar.get(Calendar.MONTH);

            switch (mMonth) {
                case 0:
                case 1:
                case 2:
                    return calendar.get(Calendar.YEAR) + "-" + 0;
                case 3:
                case 4:
                case 5:
                    return calendar.get(Calendar.YEAR) + "-" + 1;
                case 6:
                case 7:
                case 8:
                    return calendar.get(Calendar.YEAR) + "-" + 2;
                case 9:
                case 10:
                case 11:
                    return calendar.get(Calendar.YEAR) + "-" + 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int get_year_from_app_date(String dateString) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(appDateFormat.parse(dateString));
            return calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar.get(Calendar.YEAR);
    }

    public static int get_year_from_server_date(String dateString) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(serverDateFormat.parse(dateString));
            return calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar.get(Calendar.YEAR);
    }

    public static String get_yyyy_week_of_year(String dateString) {

        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(serverDateFormat.parse(dateString));
            int year = calendar.get(Calendar.YEAR);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            return year + "-" + weekOfYear;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String get_yyyy_MM(String dateString) {

        try {
            Date myDate = serverDateFormat.parse(dateString);
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
            return targetFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getLocalDate(String utcDateTime) {

        serverDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date myDate = serverDateFormat.parse(utcDateTime);
            return appDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getLocalTime(String utcDateTime) {

        serverDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date myDate = serverDateFormat.parse(utcDateTime);
            SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            return targetFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getUtcFromLocal(String localDateTime) {

        serverDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            return serverDateFormat.format(serverDateFormat.parse(localDateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String geCurrentTimeForFileName() {
        Calendar date = Calendar.getInstance();
        long currentUtcMillis = date.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd-HHmmss", Locale.ENGLISH);
        return sdf.format(currentUtcMillis);
    }
}