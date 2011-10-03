package cs2103.t14j1.storage;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;

/**
 * a basic Task and its properties
 * 
 * @author Zhuochun, Shubham
 * 
 */

public class Task {
	
    public static final String  NAME          = "name";
    public static final String  START_DATE    = "start_date";
    public static final String  END_DATE      = "end_date";
    public static final String  START_TIME    = "start_time";
    public static final String  END_TIME      = "end_time";
    public static final String  PLACE         = "place";
    public static final String  LIST          = "list_name";
    public static final String  STATUS        = "status";
    public static final String  PRIORITY      = "priority";
    
    

	private String name; // define the task action
	private String list; // belong to which list
	private Priority priority; // priority the task
	private Date startDateTime; // use Date is much easier, check out the DateFormat class
	private Date endDateTime;   // besides, long cannot use to store minutes and hours
	private boolean status; // completed or not
	
	/*
	 * this is not included in Zhuochun's first design, but it's very important
	 */
	public final String description = "";
	//Shubham: I have commented the following because I want the duration to be
	//stored as Long type
	//public final String duration = "";
	private Long startTime;
	private Long endTime;
	public static final boolean COMPLETED = true;
	public static final boolean NOT_COMPLETED = false;
	
	/**
	 * The parameters added by Shubham
	 */	
	public static final String DEADLINE = "deadline";
	public static final String DURATION = "duration";
	private Date deadline;
	private Long duration;
	private String place;
	
	/**
	 * A Constructor with all parameters provided
	 * Shubham: I have added the deadline parameter
	 */
    public Task(String name, String list, Priority priority, Date startDateTime, Date endDateTime,
            boolean status, Date deadline, Long duration, String place) {
        this.name = name;
        this.list = list;
        this.priority = priority;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = status;
        this.deadline = deadline;
        this.duration = duration;
        this.place = place;
    }
	
	public String getName() {
		return name;
	}
	
	/** Songyy's note:
	 * The list here can be a ArrayList so that we can support multiple lists
	 */
	public String getList() {
		return list;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	/**
	 * added by Shubham
	 * @return
	 */
	public Date getDeadline() {
		return deadline;
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
	 * Added by Shubham because Control.java needs to get the startDate in Date format sometimes
	 * @return
	 */
	public Date getStartDateInDateFormat() {
		return startDateTime;
	}
	
	public Date getEndDateInDateFormat() {
		return endDateTime;
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
	 *** Songyy's note:
	 *  This is not a proper name because the "Long" would be easily mistaken as
	 * a return type (though in DateFormat class, you can also see strToDateLong)
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
	
	public String getStatusStr() {
	    return status ? "Completed" : "Not Completed";
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
	
	public Long getDuration() {
		return duration;
	}
	
	/**
	 * added by Shubham
	 * @return
	 */
	public String getPlace() {
		return place;
	}

}