package logic;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Format date/time to string or otherwise Use these functions to do all the
 * things relate to time and date
 * 
 * Examples: how to use it
 *  DateFormat.getNow() to get the current date and time in type
 *  DateFormat.dateToStr(date) convert date to string
 * 
 * @author Zhuochun
 * 
 */
public class DateFormat {
    
    public static final Date todayDate = strToDate(getNowDate());

    /**
     * get current date and time without formatting
     * 
     * @return Date current Date
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * get current date and time
     * 
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String getNowDateLong() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateTime = formatter.format(currentTime);
        return nowDateTime;
    }

    /**
     * get current date in String
     * 
     * @return String yyyy-MM-dd
     */
    public static String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = formatter.format(currentTime);
        return nowDate;
    }

    /**
     * get the hour from the date passed in
     * 
     * @param date
     * @return int hour
     */
    public static int getHour(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(date);
        String hour = dateString.substring(11, 13);
        return Integer.parseInt(hour);
    }

    /**
     * get the minute from the date passed in
     * 
     * @param date
     * @return int minute
     */
    public static int getMinute(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(date);
        String min = dateString.substring(14, 16);
        return Integer.parseInt(min);
    }

    /**
     * get the hour and minute from the date passed in
     * 
     * @param date
     * @return String HH:mm
     */
    public static String getTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String nowTime = formatter.format(date);
        return nowTime;
    }

    /**
     * convert a date in String to Date format
     * 
     * String format yyyy-MM-dd HH:mm:ss
     * 
     * @param str
     * @return Date
     */
    public static Date strToDateLong(String str) {
        if (str == null)
            return null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        return strtodate;
    }

    /**
     * convert a date in String to Date format
     * 
     * String format yyyy-MM-dd HH:mm
     * 
     * @param str
     * @return Date
     */
    public static Date strToDateShort(String str) {
        if (str == null)
            return null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);

        return strtodate;
    }

    /**
     * convert a date in String to Date format
     * 
     * String format yyyy-MM-dd
     * 
     * @param str
     * @return Date
     */
    public static Date strToDate(String str) {
        if (str == null)
            return null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);

        return strtodate;
    }

    /**
     * convert string to user defined date format
     * 
     * eg. user defined format can be "yyyy-MM-dd HH:mm:ss"
     * 
     * @param format
     * @return Date
     */
    public static Date strToDate(String str, String format) {
        if (str == null)
            return null;

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);

        return strtodate;
    }

    /**
     * convert Date to string long format
     * 
     * @param date
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String dateToStrLong(Date date) {
        if (date == null)
            return null;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * convert Date to string long format
     * 
     * @param date
     * @return String yyyy-MM-dd HH:mm
     */
    public static String dateToStrShort(Date date) {
        if (date == null)
            return null;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * convert Date to string format
     * 
     * @param date
     * @return String yyyy-MM-dd
     */
    public static String dateToStr(Date date) {
        if (date == null)
            return null;
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * convert Date to user defined format
     * 
     * eg. user defined format can be "yyyy-MM-dd HH:mm:ss"
     * 
     * @param format
     * @return String
     */
    public static String dateToStr(Date date, String format) {
        if (date == null)
            return null;
        
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateStr = formatter.format(date);
        return dateStr;
    }
    
    /**
     * get the date after a number of second delay
     * 
     * eg 30 seconds after the date, delay = 30
     * 
     * @param nowTime
     * @param delay
     * @return date
     */
    public static Date getSecondAfter(Date nowTime, int delay) {
        Date newDate = new Date();

        try {
            long Time = (nowTime.getTime() / 1000) + delay;
            
            newDate.setTime(Time * 1000);
        } catch (Exception e) {
            return null;
        }

        return newDate;
    }

    /**
     * get the date after a number of minutes delay
     * 
     * eg 30 minutes after the date, delay = 30
     * 
     * @param nowTime
     * @param delay
     * @return date
     */
    public static Date getMinuteAfter(Date nowTime, int delay) {
        Date newDate = new Date();

        try {
            long Time = (nowTime.getTime() / 1000) + delay * 60;
            
            newDate.setTime(Time * 1000);
        } catch (Exception e) {
            return null;
        }

        return newDate;
    }

    /**
     * get the date after a number of days delay
     * 
     * eg 3 day after some day, then delay = 3
     * 
     * @param nowdate
     * @param delay
     * @return date
     */
    public static Date getDateAfter(Date nowdate, int delay) {
        Date newDate = new Date();
        
        try {
            long myTime = (nowdate.getTime() / 1000) + delay * 24 * 60 * 60;
            
            newDate.setTime(myTime * 1000);
        } catch (Exception e) {
            return null;
        }
        
        return newDate;
    }

    /**
     * check if the passed date is in a leap year
     * 
     * @param date
     *            in the format of yyyy-MM-dd
     * @return boolean
     */
    public static boolean isLeapYear(String date) {
        Date d = strToDate(date);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();

        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);

        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        }

        return false;
    }

    /**
     * get the number of days in a month (pass in a Date)
     * 
     * @param date
     *            in the format of yyyy-MM-dd
     * @return number of days
     */
    public static int getDaysInMonth(String date) {
        String mon = date.substring(5, 7);
        int month = Integer.parseInt(mon);

        if (month == 2 && isLeapYear(date)) {
            return 29;
        }

        int[] days = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        return days[month];
    }

    /**
     * check whether the two dates passed in are in the same week
     * 
     * @param date1
     * @param date2
     * @return boolean
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (subYear == 0) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) {
            // if the last week in December across to next year
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }

        return false;
    }

}