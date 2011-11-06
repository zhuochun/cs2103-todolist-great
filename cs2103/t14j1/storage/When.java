package cs2103.t14j1.storage;

import java.util.Calendar;
import java.util.Date;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.user.User;

/**
 * class deal with date and time properties in Task
 * 
 * It makes sure that the date/time follows the instructions on adding a Task
 * 
 * @author Zhuochun
 */
public class When implements Comparable<Object> {
    public Calendar startDateTime;
    public Calendar endDateTime;
    public Calendar deadline;
    public Boolean  isAllDay;
    
    public static final boolean ALL_DAY_TASK   = true;
    public static final boolean DATE_TIME_TASK = false;
    
    // Exception Strings
    private static final String EXCEPTION_DATETIME = "Start date and time must be earlier than end date time";
     
    /**
     * constructor
     */
    public When() {
        startDateTime = null;
        endDateTime   = null;
        deadline      = null;
        isAllDay      = true;
    }
    
    /**
     * set all parameters, old format
     * 
     * @param start
     * @param end
     * @param dl
     * @param duration
     */
    public When(Date start, Date end, Date dl, Long duration) {
        setAll(start, end, dl, duration);
    }
    
    /**
     * clone a When object
     */
    public Object clone() {
        When cloneWhen = new When();
        
        cloneWhen.setType(isAllDay);
        if (startDateTime != null) {
            cloneWhen.setStartDateTime(startDateTime.getTime());
            cloneWhen.setEndDateTime(endDateTime.getTime());
        }
        if (deadline != null) {
            cloneWhen.setDeadline(deadline.getTime());
        }
        
        return cloneWhen;
    }
    
    /**
     * set whether it is an All-Day task (Date - Date) or DateTime task (DateTime - DateTime)
     * 
     * Use parameter:
     *  When.ALL_DAY_TASK : All-Day Task
     *  When.DATE_TIME_TASK : DateTime Task 
     */
    public void setType(Boolean b) {
        isAllDay = b;
    }

    /**
     * set all parameters, to be backward compatible
     * 
     * startDateTime should be early than endDateTime, otherwise exception will be thrown
     * 
     * @param start
     * @param end
     * @param dl
     * @param duration
     */
    public void setAll(Date start, Date end, Date dl, Long duration) {
        // check start/end time invalid case
        if (start != null && end != null && end.before(start)) {
            throw new IllegalArgumentException(EXCEPTION_DATETIME);
        }
        
        // set isAllDay task
        if (duration == null) {
            setType(ALL_DAY_TASK);
        } else {
            setType(DATE_TIME_TASK);
        }
        
        // set deadline
        if (dl != null) {
            setDeadline(dl);
        }
        
        // set startDateTime
        if (start != null) {
            setStartDateTime(start);
        }
        
        // set endDateTime
        if (end != null) {
            setEndDateTime(end);
        }
    }
    
    /**
     * set startDateTime, you need to call setType() to set task type (all-day or dateTime)
     * 
     * Note:
     * 1. if endDateTime is not set, will be initialised the same as startDateTime
     * 2. if the endDateTime is already set, but earlier than startDateTime,
     *    endDateTime will be reset the same to startDateTime
     * 
     * @param date
     */
    public void setStartDateTime(Date date) {
        if (date == null) {
            startDateTime = null;
            endDateTime   = null;
            return ;
        } else if (endDateTime == null || endDateTime.getTime().before(date)) {
            endDateTime = Calendar.getInstance();
            endDateTime.setTime(date);
            clear(endDateTime, CLEAR_BELOW_SECOND);
        }
        
        if (startDateTime == null) {
            startDateTime = Calendar.getInstance();
        }
        
        startDateTime.setTime(date);
        clear(startDateTime, CLEAR_BELOW_SECOND);
    }
    
    /**
     * set all date, time for startDateTime
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * Note:
     * 1. if endDateTime is not set, will be initialised the same as startDateTime
     * 2. if the endDateTime is already set, but earlier than startDateTime,
     *    endDateTime will be reset the same to startDateTime
     * 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public void setStartDateTime(int year, int month, int day, int hour, int minute) {
        setStartDate(year, month, day);
        setStartTime(hour, minute);
    }
    
    /**
     * set date for startDateTime, time will be set as 00:00:00
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * Note:
     * 1. if endDateTime is not set, will be initialised the same as startDateTime
     * 2. if the endDateTime is already set, but earlier than startDateTime,
     *    endDateTime will be reset the same to startDateTime
     * 
     * @param year
     * @param month
     * @param day
     */
    public void setStartDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        
        if (year != -1)
            date.set(Calendar.YEAR, year);
        if (month != -1)
            date.set(Calendar.MONTH, month);
        if (day != -1)
            date.set(Calendar.DATE, day);
        
        clear(date, CLEAR_BELOW_HOUR);
        
        if (endDateTime == null || endDateTime.getTime().before(date.getTime())) {
            endDateTime = (Calendar) date.clone();
        }
        
        startDateTime = date;
        isAllDay      = true;
    }
    
    /**
     * set time for startDateTime, if startDate is not set earlier, it will taken as today
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * @param hour
     * @param minute
     */
    public void setStartTime(int hour, int minute) {
        Calendar date = startDateTime;
        
        if (date == null) {
            date = Calendar.getInstance();
        }
        
        if (hour != -1)
            date.set(Calendar.HOUR_OF_DAY, hour);
        if (minute != -1)
            date.set(Calendar.MINUTE, minute);
        
        clear(date, CLEAR_BELOW_SECOND);
        
        if (endDateTime == null || endDateTime.getTime().before(date.getTime())) {
            endDateTime = (Calendar) date.clone();
        }
        
        startDateTime = date;
        isAllDay      = false;
    }
    
    /**
     * set endDateTime, you need to call setType() to set task type (all-day or dateTime)
     * 
     * Note:
     * 1. if startDateTime is not set, will be initialised the same as endDateTime
     * 2. if the startDateTime is already set, but later than endDateTime, will thrown exception
     * 
     * @param date
     */
    public void setEndDateTime(Date date) {
        if (date == null) {
            startDateTime = null;
            endDateTime   = null;
            return ;
        } else if (startDateTime != null && date.before(startDateTime.getTime())) {
            throw new IllegalArgumentException(EXCEPTION_DATETIME);
        } else if (startDateTime == null) {
            startDateTime = Calendar.getInstance();
            startDateTime.setTime(date);
            clear(startDateTime, CLEAR_BELOW_SECOND);
        }
        
        if (endDateTime == null) {
            endDateTime = Calendar.getInstance();
        }
        
        endDateTime.setTime(date);
        clear(endDateTime, CLEAR_BELOW_SECOND);
    }
    
    /**
     * set all date, time for endDateTime
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * Note:
     * 1. if startDateTime is not set, will be initialised the same as endDateTime
     * 2. if the startDateTime is already set, but later than endDateTime, will thrown exception
     * 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public void setEndDateTime(int year, int month, int day, int hour, int minute) {
        setEndDate(year, month, day);
        setEndTime(hour, minute);
    }
    
    /**
     * set date for endDateTime, time will be set as 00:00:00
     * 
     * Note:
     * 1. if startDateTime is not set, will be initialised the same as endDateTime
     * 2. if the startDateTime is already set, but later than endDateTime, will thrown exception
     * 
     * @param year
     * @param month
     * @param day
     */
    public void setEndDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        
        if (year != -1)
            date.set(Calendar.YEAR, year);
        if (month != -1)
            date.set(Calendar.MONTH, month);
        if (day != -1)
            date.set(Calendar.DATE, day);
        
        clear(date, CLEAR_BELOW_HOUR);
        
        if (startDateTime != null && date.getTime().before(startDateTime.getTime())) {
            throw new IllegalArgumentException(EXCEPTION_DATETIME);
        } else if (startDateTime == null) {
            startDateTime = (Calendar) date.clone();
        }
        
        endDateTime = date;
        isAllDay    = true;
    }
    
    /**
     * set time for endDateTime, if date is not set earlier, it will taken as today
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * Note:
     * 1. if startDateTime is not set, will be initialised the same as endDateTime
     * 2. if the startDateTime is already set, but later than endDateTime, will thrown exception
     * 
     * @param hour
     * @param minute
     */
    public void setEndTime(int hour, int minute) {
        Calendar date = endDateTime;
        
        if (date == null) {
            date = Calendar.getInstance();
        }
        
        if (hour != -1)
            date.set(Calendar.HOUR_OF_DAY, hour);
        if (minute != -1)
            date.set(Calendar.MINUTE, minute);
        
        clear(date, CLEAR_BELOW_SECOND);
        
        if (startDateTime != null && date.getTime().before(startDateTime.getTime())) {
            throw new IllegalArgumentException(EXCEPTION_DATETIME);
        } else if (startDateTime == null) {
            startDateTime = (Calendar) date.clone();
        }
        
        endDateTime = date;
        isAllDay    = false;
    }
    
    /**
     * set deadline from a date
     * 
     * @param date
     */
    public void setDeadline(Date date) {
        if (date == null) {
            deadline = null;
            return ;
        } else if (deadline == null) {
            deadline = Calendar.getInstance();
        }
        
        deadline.setTime(date);
        
        clear(deadline, CLEAR_BELOW_SECOND);
    }

    /**
     * set deadline date, time will be set as 23:59:00
     * 
     * if any of the parameter is -1, it will set as today's value.
     * 
     * @param year
     * @param month
     * @param day
     */
    public void setDeadline(int year, int month, int day) {
        if (deadline == null) {
            deadline = Calendar.getInstance();
        }
        
        if (year != -1)
            deadline.set(Calendar.YEAR, year);
        if (month != -1)
            deadline.set(Calendar.MONTH, month);
        if (day != -1)
            deadline.set(Calendar.DATE, day);
        
        deadline.set(Calendar.HOUR_OF_DAY, 23);
        deadline.set(Calendar.MINUTE, 59);
        
        clear(deadline, CLEAR_BELOW_SECOND);
    }
    
    /**
     * set deadline date and time
     * 
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public void setDeadline(int year, int month, int day, int hour, int minute) {
        if (deadline == null) {
            deadline = Calendar.getInstance();
        }
        
        if (year != -1)
            deadline.set(Calendar.YEAR, year);
        if (month != -1)
            deadline.set(Calendar.MONTH, month);
        if (day != -1)
            deadline.set(Calendar.DATE, day);
        if (hour != -1)
            deadline.set(Calendar.HOUR_OF_DAY, hour);
        if (minute != -1)
            deadline.set(Calendar.MINUTE, minute);
        
        clear(deadline, CLEAR_BELOW_SECOND);
    }
    
    /**
     * Must have <code>setType(DATE_TIME_TASK)<code> set, otherwise duration may be ignored
     * if this is an all-day task
     * 
     * setDuration will overwrite endDateTime if the duration is different from old duration
     * 
     * @param duration in second
     */
    public void setDuration(Long duration) {
        if (isAllDay) {
            return ;
        } else if (duration == null) {
            isAllDay = true;
            return ;
        } else if (getStartDateTime() == null) {
            return ;
        } else if (getDuration() == duration) {
            return ;
        }
        
        duration = duration * 1000;
        
        Long newEnd = startDateTime.getTimeInMillis() + duration;
        
        endDateTime.setTimeInMillis(newEnd);
    }
    
    /**
     * check whether it is an All day task
     * 
     * @return true if it is a all day task
     */
    public boolean isAllDay() {
        return isAllDay;
    }
    
    /**
     * check whether it has a deadline
     * 
     * @return true if it has a deadline
     */
    public boolean hasDeadline() {
        return deadline != null;
    }
    
    /**
     * check whether it has startDateTime, endDateTime
     * 
     * @return true if it has
     */
    public boolean hasDateTime() {
        return startDateTime != null;
    }
    
    /**
     * get the startDateTime in Date format
     * 
     * @return startDateTime
     */
    public Date getStartDateTime() {
        if (startDateTime != null)
            return startDateTime.getTime();
        else
            return null;
    }
    
    /**
     * get the endDateTime in Date format
     * 
     * @return endDateTime
     */
    public Date getEndDateTime() {
        if (endDateTime != null)
            return endDateTime.getTime();
        else
            return null;
    }
    
    /**
     * get the duration in Long format
     * 
     * @return duration
     */
    public Long getDuration() {
        if (startDateTime == null || endDateTime == null || isAllDay) {
            return null;
        } else {
            Long duration = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
            return duration / 1000;
        }
    }
    
    /**
     * get the duration even it is an all-day task
     * 
     * @return duration
     */
    private Long getDurationFull() {
        if (startDateTime == null || endDateTime == null) {
            return null;
        } else {
            Long duration = endDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
            return duration / 1000;
        }
    }
    
    /**
     * get the duration in human readable format
     * 
     * @return duration String
     */
    public String getDurationStr() {
        Long duration = getDurationFull();
        
        if (duration == null) {
            return null;
        }
        
        String result = buildTimeDecadeStr(duration.intValue(), User.isUseAbbreviate());

        if (result.length() == 0) {
            if (isAllDay) {
                result = "All Day";
            } else {
                result = "0 Minutes";
            }
        }

        return result;
    }
    
    public String getDeadlineStr() {
        if (deadline == null) {
            return null;
        }
        
        StringBuffer str = new StringBuffer();
        
        Long time = (deadline.getTimeInMillis() - DateFormat.getNow().getTime()) / 1000;
        
        String result = buildTimeDecadeStr(time.intValue(), User.isUseAbbreviate());
        
        if (result.isEmpty()) {
            str.append("< 0 Minute ");
        } else {
            str.append(result);
        }
        
        if (time == 0) {
            str.append("Now");
        } else if (time > 0) {
            str.append("Later");
        } else {
            str.append("Ago");
        }
        
        return str.toString();
    }
    
    private String buildTimeDecadeStr(int time, boolean abbr) {
        final String[] fullName = {"Day", "Hour", "Minute"};
        final String[] abbrName = {"D", "H", "M"};
        
        if (time < 0) { time = -time; }
        
        int[] result = new int[3];
        
        result[0] = time / 86400;
        time = time - result[0] * 86400;
        result[1] = time / 3600;
        time = time - result[1] * 3600;
        result[2] = time / 60;

        StringBuffer dStr = new StringBuffer();

        for (int i = 0; i < 3; i++) {
            if (result[i] != 0) {
                dStr.append(result[i]);
                
                if (abbr) {
                    dStr.append(abbrName[i]);
                } else {
                    dStr.append(" ");
                    dStr.append(fullName[i]);
                }
                
                if (result[i] > 1) {
                    dStr.append("s");
                }
                
                dStr.append(" ");
            }
        }
        
        return dStr.toString();
    }
    
    /**
     * get the deadline in Date format
     * 
     * @return deadline
     */
    public Date getDeadline() {
        if (deadline != null)
            return deadline.getTime();
        else
            return null;
    }
    
    public static void clear(Calendar c, int t) {
        switch (t) {
            case CLEAR_BELOW_HOUR:
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                // no break, so can clear below second as well
            case CLEAR_BELOW_SECOND:
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
    }
    
    public static final int CLEAR_BELOW_SECOND = 0;
    public static final int CLEAR_BELOW_HOUR   = 1;

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * @author songyy
	 * this When class is supposed to be non-comparable; this method
	 * is implemented to support the use of TreeMap on When class
	 * 
	 * Used by GCalSyn
	 */
	@Override
	public int compareTo(Object b) {
		When c = (When) b;	// b is the type of when
		if(this.getStartDateTime() == null && c.getStartDateTime() == null){
			return 0;
		} else if(this.getStartDateTime() == null){
			return -1;
		} else if(c.getStartDateTime() == null){
			return 1;
		} else if(this.getStartDateTime().before(c.getStartDateTime())){
			return -1;
		} else if(this.getStartDateTime().after(c.getStartDateTime())){
			return 1;
		} else if(this.getEndDateTime() == null && c.getEndDateTime() == null){
			return 0;
		} else if(this.getEndDateTime() == null){
			return -1;
		} else if(c.getEndDateTime() == null){
			return 1;
		} else if(this.getEndDateTime().before(c.getEndDateTime())){
			return -1;
		} else if(this.getEndDateTime().after(c.getEndDateTime())){
			return 1;
		} else{
			return 0;
		}
	}
}