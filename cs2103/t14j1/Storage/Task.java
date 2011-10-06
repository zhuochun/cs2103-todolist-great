package cs2103.t14j1.storage;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;

/**
 * a basic Task and its properties
 * 
 * @author Zhuochun, Shubham
 * 
 */
public class Task extends AbstractModelObject {
	
    // used as XML tag names
    public static final String  NAME       = "name";
    public static final String  LIST       = "list_name";
    public static final String  PLACE      = "place";
    public static final String  PRIORITY   = "priority";
    public static final String  START_DATE = "start_date";
    public static final String  END_DATE   = "end_date";
	public static final String  DEADLINE   = "deadline";
    public static final String  STATUS     = "status";
	public static final String  DURATION   = "duration";

	// private members
	private String   name;          // define the task action
	private String   place;         // define the place of task
	private String   list;          // belong to which list
	private Priority priority;      // priority of the task
	private Date     startDateTime; // start date and time
	private Date     endDateTime;   // end date and time
	private Date     deadline;      // deadline date and time
	private Long     duration;      // duration of task
	private boolean status;        // completed or not
	
	public static final boolean COMPLETED   = true;
	public static final boolean INCOMPLETED = false;
	
	/**
	 * A Constructor with all parameters provided
	 */
    public Task(String name, String place, String list, Priority priority,
            Date startDateTime, Date endDateTime, Date deadline, Long duration, boolean status) {
        this.name = name;
        this.place = place;
        this.list = list;
        this.priority = priority;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.deadline = deadline;
        this.duration = duration;
        this.status = status;
    }
    
    /**
     * A Constructor with name and list provided only
     */
    public Task(String name, String list) {
        this.name = name;
        this.place = null;
        this.list = list;
        this.priority = Priority.NORMAL;
        this.startDateTime = null;
        this.endDateTime = null;
        this.deadline = null;
        this.duration = null;
        this.status = INCOMPLETED;
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
	    String oldName = name;
	    
	    name = newName;
	    
	    firePropertyChange("name", oldName, name);
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setPlace(String newPlace) {
	    String oldPlace = place;
	    
	    place = newPlace;
	    
	    firePropertyChange("place", oldPlace, place);
	}

	public String getList() {
		return list;
	}
	
	public void setList(String newList) {
	    String oldList = list;
	    
	    list = newList;
	    
	    firePropertyChange("list", oldList, list);
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public String getPriorityStr() {
	    return priority.toString();
	}
	
	public void setPriority(Priority newValue) {
	    Priority oldValue = priority;
	    
	    priority = newValue;
	    
	    firePropertyChange("priority", oldValue, newValue);
	}
	
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(Date newDate) {
	    Date oldDate = startDateTime;
	    
	    startDateTime = newDate;
	    
	    firePropertyChange("startDateTime", oldDate, newDate);
	}
	
	/**
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public String getStartLong() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStrLong(startDateTime);
	}
	
	/**
	 * @return yyyy-MM-dd
	 */
	public String getStartDate() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStr(startDateTime);
	}
	
	/**
	 * @return HH:mm
	 */
	public String getStartTime() {
		if (startDateTime == null) {
			return null;
		}
		
		return DateFormat.getTime(startDateTime);
	}
	
	public Date getEndDateTime() {
		return endDateTime;
	}
	
	public void setEndDateTime(Date newDate) {
	    Date oldDate = endDateTime;
	    
	    endDateTime = newDate;
	    
	    firePropertyChange("endDateTime", oldDate, newDate);
	}
	
	/**
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public String getEndLong() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStrLong(endDateTime);
	}
	
	/**
	 * @return yyyy-MM-dd
	 */
	public String getEndDate() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.dateToStr(endDateTime);
	}
	
	/**
	 * @return HH:mm
	 */
	public String getEndTime() {
		if (endDateTime == null) {
			return null;
		}
		
		return DateFormat.getTime(endDateTime);
	}
	
    public Date getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Date newDate) {
        Date oldDate = deadline;
        
        deadline = newDate;
        
        firePropertyChange("deadline", oldDate, newDate);
    }
    
    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String getDeadlineLong() {
        if (deadline == null) {
            return null;
        }
        
        return DateFormat.dateToStrLong(deadline);
    }
    
    /**
     * @return yyyy-MM-dd
     */
    public String getDeadlineDate() {
        if (deadline == null) {
            return null;
        }
        
        return DateFormat.dateToStr(deadline);
    }
    
    /**
     * @return HH:mm
     */
    public String getDeadlineTime() {
		if (deadline == null) {
			return null;
		}
		
		return DateFormat.getTime(deadline);
    }
    
    public Long getDuration() {
        return duration;
    }
    
    public void setDuration(Long newDuration) {
        Long oldDuration = duration;
        
        duration = newDuration;
        
        firePropertyChange("duration", oldDuration, newDuration);
    }
    
	public boolean getStatus() {
		return status;
	}
	
	public String getStatusStr() {
	    return status ? "Completed" : "Incompleted";
	}
	
	public void setStatus(boolean newStatus) {
	    boolean oldStatus = status;
	    
		status = newStatus;
		
		firePropertyChange("status", oldStatus, status);
	}
	
}