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
	Integer time;
	
	public void setDate(Calendar date){
		this.date = date;
	}
	
	public void setDate(Date date){
		this.date = Calendar.getInstance();
		this.date.setTime(date);
	}
	
	public void setTime(Integer time){
		this.time = time;
	}
	
	public Date getDate(){
		clearTimeFieldForDate(this.date);
		
		if(date == null)	return null;
		if(time != null){
			date.set(Calendar.SECOND, time);
		}
		
		return date.getTime();
	}
	
	public Integer getTime(){
		return this.time;
	}
	
	public static void clearTimeFieldForDate(Calendar date){
		date.set(Calendar.MILLISECOND, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.HOUR_OF_DAY, 0);
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