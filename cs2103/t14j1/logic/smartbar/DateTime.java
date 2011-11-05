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
	int dateToShift = 0;
	
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
	
	public Date getDateWithTime(){
		if(date == null)	return null;
		setTimeOfDateToTime();
		date.add(Calendar.DATE, dateToShift);
		return date.getTime();
	}
	
	public void setTimeOfDateToTime() {
		if(this.date == null || this.time == null)	return;

		clearTimeFieldForDate(this.date);
		date.set(Calendar.SECOND, (int)(long)time);
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
	
	public void setTimeOfDateToFirstSecOnTimeNull(){
		int lastSec = (SEC_PER_HOUR * HOUR_PER_DAY - 1);
		clearTimeFieldForDate(date);
	}
	
	public void setTimeOfDateToLastSecOnTimeNull(){
		if(date == null)	return;
		int lastSec = (SEC_PER_HOUR * HOUR_PER_DAY - 1);
		clearTimeFieldForDate(date);
		date.set(Calendar.SECOND, lastSec);
	}
	
	public long diff(DateTime b){
		return (int) ((this.getDateWithTime().getTime() - b.getDateWithTime().getTime())/1000);
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
		if(dateTime.getDateWithTime() == null)	return true;
		else return this.getDateWithTime().before(dateTime.getDateWithTime());
	}

	public void optionallyAddOneDayBasedOnCurrentTime() {
		if(this.date == null)	return;
		
		if(currentTime.isTimeInTheAfterNoon() &&
				!this.isTimeInTheAfterNoon()){
			this.date.add(Calendar.DATE, 1);
		}
	}

	public void setDateToBeCurrentTime() {
		this.date = Calendar.getInstance();
	}

	public void addSecToTime(Long secs) {
		if(this.time == null)	return;
		this.time += secs;
		if(this.time > HOUR_PER_DAY * SEC_PER_HOUR - 1){
			this.time -= HOUR_PER_DAY * SEC_PER_HOUR;
			dateToShift += 1;
		}
	}
}

