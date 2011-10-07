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
	public static final boolean INCOMPLETE = false;
	
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
    public Task() {
        this.name = null;
        this.place = null;
        this.list = TaskLists.INBOX;
        this.priority = Priority.NORMAL;
        this.startDateTime = null;
        this.endDateTime = null;
        this.deadline = null;
        this.duration = null;
        this.status = INCOMPLETE;
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
	    name = newName;
	}
	
	public String getPlace() {
		return place;
	}
	
	public void setPlace(String newPlace) {
	    place = newPlace;
	}

	public String getList() {
		return list;
	}
	
	public void setList(String newList) {
	    list = newList;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public String getPriorityStr() {
	    return priority.toString();
	}
	
	public void setPriority(Priority newValue) {
	    priority = newValue;
	}
	
	public String getDate() {
	    if (startDateTime != null & endDateTime != null) {
	        StringBuffer fullstr = new StringBuffer();
	        
	        fullstr.append(getStartLong());
	        fullstr.append(" - ");
	        fullstr.append(getEndLong());
	        
	        return fullstr.toString();
	    }
	    
	    return null;
	}
	
	public Date getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(Date newDate) {
	    startDateTime = newDate;
	}
	
	/**
	 * @return yyyy-MM-dd HH:mm
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
	    endDateTime = newDate;
	}
	
	/**
	 * @return yyyy-MM-dd HH:mm
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
        deadline = newDate;
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
        duration = newDuration;
    }
    
	public String getDurationStr() {
	    if (duration == null) {
	        return null;
	    }
	    
	    int d         = duration.intValue();
        int hours     = d / 3600;
        int minutes   = (d - hours * 3600) / 60;
	    
        StringBuffer dStr = new StringBuffer();
        
        if (hours != 0) {
            dStr.append(hours + "H");
        }
        
        if (minutes != 0) {
            dStr.append(minutes + "M");
        }
        
        return dStr.toString();
    }

    public boolean getStatus() {
		return status;
	}
	
	public String getStatusStr() {
	    return status ? "Completed" : "Incomplete";
	}
	
	public void setStatus(boolean newStatus) {
		status = newStatus;
	}
	
	public String toString() {
	    StringBuilder str = new StringBuilder();
	    
	    str.append(name);
	    str.append(" | ");
	    str.append(getPriorityStr());
	    str.append(" | ");
	    str.append(getStatus());
	    
	    return str.toString();
	}
	
}