package cs2103.t14j1.storage;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;

/**
 * a basic Task and its properties
 * 
 * @author Zhuochun
 *
 */
public class Task {

	private String name; // define the task action
	private String list; // belong to which list
	private Priority priority;
	private Date startDateTime; // use Date is much easier, check out the DateFormat class
	private Date endDateTime;   // besides, long cannot use to store minutes and hours
	private boolean status; // completed or not
	
	public static final boolean COMPLETED = true;
	public static final boolean NOT_COMPLETED = false;
	
	/**
	 * A Constructor with all parameters provided
	 */
	public Task (String name, String list, Priority priority, Date startDateTime,
			Date endDateTime, boolean status) {
		this.name      = name;
		this.list      = list;
		this.priority  = priority;
		this.startDateTime = startDateTime;
		this.endDateTime   = endDateTime;
		this.status    = status;
	}
	
	public String getName() {
		return name;
	}
	
	public String getList() {
		return list;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	/**
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public String getStartLong() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStrLong(startDateTime);
	}
	
	/**
	 * 
	 * @return yyyy-MM-dd
	 */
	public String getStartDate() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStr(startDateTime);
	}
	
	/**
	 * 
	 * @return HH:mm
	 */
	public String getStartTime() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.getTime(startDateTime);
	}
	
	/**
	 * 
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public String getEndLong() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStrLong(endDateTime);
	}

	/**
	 * 
	 * @return yyyy-MM-dd
	 */
	public String getEndDate() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStr(endDateTime);
	}
	
	/**
	 * 
	 * @return HH:mm
	 */
	public String getEndTime() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.getTime(endDateTime);
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean newStatus) {
		status = newStatus;
	}
	
	// TODO change this
	public String toString() {
		return name;
	}
	
	// TODO change this
	public String[] toArray() {
		return new String[2];
	}

}