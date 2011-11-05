package cs2103.t14j1.logic.smartbar;

import java.util.Calendar;
import java.util.Date;


/**
 * @author Song Yangyu
 * A date time wrapper for internal use
 * Creating this wrapper for internal use now; it can be move to the task
 *  package if others found it useful
 */
public class DateTime {
	Calendar date;
	Long time;
	
	public static int SEC_PER_MINUTE 	= 60;
	public static int SEC_PER_HOUR		= 3600;
	public static int HOUR_PER_HALF_DAY	= 12;
	public static int HOUR_PER_DAY		= 24;
	
	public static final DateTime currentTime = DateTime.getInstance();
	
	public void setDate(Calendar date){
		this.date = date;
	}
	
	public void setDate(Date date){
		this.date = Calendar.getInstance();
		this.date.setTime(date);
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public Date getDateInDateTypeWithTime(){
		if(date == null)	return null;
		setTimeOfDateToTime();
		return date.getTime();
	}
	
	public void setTimeOfDateToTime() {
		if(this.date == null)	return;

		clearTimeFieldForDate(this.date);
		if(time != null){
			date.set(Calendar.SECOND, (int)(long)time);
		}
	}

	public void onDateNullDefaultTodayOrNextDayBasedOnTime(){
		if(date == null)	date = Calendar.getInstance();
		setTimeOfDateToTime();
	}
	
	
	public Long getTime(){
		return this.time;
	}
	
	public static void clearTimeFieldForDate(Calendar date){
		if(date == null)	return;
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.HOUR_OF_DAY, 0);
	}

	public static int getCurrentTimeInSec() {
		return (int) (Calendar.getInstance().getTimeInMillis()/1000);
	}

	public void setTimeToFirstSecOnNull() {
		if(this.time == null)	this.setTimeToFirstSec();
	}

	public void setTimeToLastSecOnNull(){
		if(this.time == null)	this.setTimeToLastSec(); 
	}
	
	public void setTimeToFirstSec(){
		this.time = 0L;
	}
	
	public void setTimeToLastSec(){
		this.time = (long) (SEC_PER_HOUR * HOUR_PER_DAY - 1);
	}
	
	public long diff(DateTime b){
		return (int) ((this.getDateInDateTypeWithTime().getTime() - b.getDateInDateTypeWithTime().getTime())/1000);
	}

	public boolean isTimeInTheAfterNoon() {
		return this.time < HOUR_PER_HALF_DAY * SEC_PER_HOUR;
	}

	public static DateTime getInstance() {
		DateTime res = new DateTime();
		res.date = Calendar.getInstance();
		res.time = 
			(res.date.getTimeInMillis()/1000) % (HOUR_PER_DAY * SEC_PER_HOUR);
		return res;
	}

	public boolean isBefore(DateTime dateTime) {
		if(dateTime.getDateInDateTypeWithTime() == null)	return true;
		else return this.getDateInDateTypeWithTime().before(dateTime.getDateInDateTypeWithTime());
	}

	public void optionallyAddOneDayBasedOnCurrentTime() {
		if(currentTime.isTimeInTheAfterNoon() &&
				!this.isTimeInTheAfterNoon()){
			this.date.add(Calendar.DATE, 1);
		}
	}
}


/*
class Time implements Comparable<Time>{
	private Long time = null;
	
	Time(){
		this.time = null;
	}
	
	Time(Long time){
		this.time = time;
	}
	
	public void setTime(Long time){
		this.time = time;
	}
	
	public void setTime(Integer time){
		this.time = (long)time;
	}
	
	public Long getTime(){
		return this.time;
	}
	
	public int compareTo(Time b){
		return (int)(this.getTime() - b.getTime());
	}
}
*/