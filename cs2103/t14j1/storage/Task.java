package cs2103.t14j1.storage;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.gCal.GCalSyn;

/**
 * a basic Task and its properties
 * 
 * @author Zhuochun
 * 
 */
public class Task implements Comparable<Object> {

    // private members
    private String              name;              // define the task action
    private String              place;             // define the place of task
    private String              list;              // belong to which list
    private Priority            priority;          // priority of the task
    private When                when;             // stores start/end, duration, deadline
    private boolean             status;            // completed or not
    
    // additional parameter to note if it's to be synced with gCalendar
    private int syncWithGCal = GCalSyn.NOT_SYN;
    private String gCalId = null;
	private Date lastEditTime;

    public static final boolean COMPLETED  = true;
    public static final boolean INCOMPLETE = false;

    // Exceptions Strings
    private static final String EXCEPTION_EMPTY_TASK_NAME = "Task name cannot be empty";
    private static final String EXCEPTION_EMPTY_LIST_NAME = "Task must belong to a list with non-empty name";
    private static final String EXCEPTION_NULL_WHEN       = "Task's When property cannot be null";

    /**
     * A Constructor with all parameters provided
     */
    public Task(String name, String place, String list, Priority priority, Date startDateTime, Date endDateTime,
            Date deadline, Long duration, boolean status) {
        this();
        setName(name);
        setPlace(place);
        setList(list);
        setPriority(priority);
        setWhen(new When(startDateTime, endDateTime, deadline, duration));
        setStatus(status);
    }
    
    /**
     * new constructor with all parameters
     */
    public Task(String name, String place, String list, Priority priority, When when, boolean status) {
        this();
        setName(name);
        setPlace(place);
        setList(list);
        setPriority(priority);
        setWhen(new When());
        setStatus(status);
    }

    /**
     * A Constructor with name and list provided only
     */
    public Task() {
        this.name          = "";
        this.place         = null;
        this.list          = TaskLists.INBOX;
        this.priority      = Priority.NORMAL;
        this.when          = new When();
        this.status        = INCOMPLETE;
        this.lastEditTime  = new Date();	// mark the last edit time to be the time when created
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_TASK_NAME);
        }
    
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
        if (newList == null) {
            newList = TaskLists.INBOX;
        } else if (newList.trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_LIST_NAME);
        }

        list = newList;
    }
    
    public When getWhen() {
        return when;
    }
    
    public void setWhen(When newWhen) {
        if (newWhen == null) {
            throw new NullPointerException(EXCEPTION_NULL_WHEN);
        }

        when = newWhen;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getPriorityStr() {
        return priority.toString();
    }

    public void setPriority(Priority newValue) {
        if (newValue == null) {
            newValue = Priority.NORMAL;
        }

        priority = newValue;
    }

    public String getStartEndDate() {
        if (when.hasDateTime()) {
            StringBuffer fullstr = new StringBuffer();

            fullstr.append(getStartDate());
            
            if (!when.isAllDay()) {
                fullstr.append(" ");
                fullstr.append(getStartTime());
            }
            
            fullstr.append(" - ");
            fullstr.append(getEndDate());
            
            if (!when.isAllDay()) {
                fullstr.append(" ");
                fullstr.append(getEndTime());
            }

            return fullstr.toString();
        }

        return null;
    }

    public Date getStartDateTime() {
        return when.getStartDateTime();
    }

    public void setStartDateTime(Date newDate) {
        when.setStartDateTime(newDate);
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String getStartLong() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStrLong(when.getStartDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd HH:mm
     */
    public String getStartShort() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStrShort(when.getStartDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd
     */
    public String getStartDate() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStr(when.getStartDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return HH:mm
     */
    public String getStartTime() {
        if (when.hasDateTime() && !when.isAllDay()) {
            return DateFormat.getTime(when.getStartDateTime());
        } else {
            return null;
        }
    }

    public Date getEndDateTime() {
        return when.getEndDateTime();
    }

    public void setEndDateTime(Date newDate) {
        when.setEndDateTime(newDate);
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String getEndLong() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStrLong(when.getEndDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd HH:mm
     */
    public String getEndShort() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStrShort(when.getEndDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd
     */
    public String getEndDate() {
        if (when.hasDateTime()) {
            return DateFormat.dateToStr(when.getEndDateTime());
        } else {
            return null;
        }
    }

    /**
     * @return HH:mm
     */
    public String getEndTime() {
        if (when.hasDateTime() && !when.isAllDay()) {
            return DateFormat.getTime(when.getEndDateTime());
        } else {
            return null;
        }
    }

    public Date getDeadline() {
        return when.getDeadline();
    }

    public void setDeadline(Date newDate) {
        when.setDeadline(newDate);
    }

    /**
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String getDeadlineLong() {
        if (when.hasDeadline()) {
            return DateFormat.dateToStrLong(when.getDeadline());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd HH:mm
     */
    public String getDeadlineShort() {
        if (when.hasDeadline()) {
            return DateFormat.dateToStrShort(when.getDeadline());
        } else {
            return null;
        }
    }

    /**
     * @return yyyy-MM-dd
     */
    public String getDeadlineDate() {
        if (when.hasDeadline()) {
            return DateFormat.dateToStr(when.getDeadline());
        } else {
            return null;
        }
    }

    /**
     * @return HH:mm
     */
    public String getDeadlineTime() {
        if (when.hasDeadline()) {
            return DateFormat.getTime(when.getDeadline());
        } else {
            return null;
        }
    }
    
    public boolean isWithinPeriod(Date start, Date end) {
        boolean result = false;
        
        if (start == null && end == null) {
            result = true;
        } else if (when.hasDateTime()) {
            if (start != null && end != null) {
                result = start.before(when.getStartDateTime()) && end.after(when.getEndDateTime());
            } else if (start == null) {
                result = end.after(when.getEndDateTime());
            } else if (end == null) {
                result = start.before(when.getStartDateTime());
            }
        } else if (when.hasDeadline()) {
            if (start != null && end != null) {
                result = start.before(when.getDeadline()) && end.after(when.getDeadline());
            } else if (start == null) {
                result = end.after(when.getDeadline());
            } else if (end == null) {
                result = start.before(when.getDeadline());
            }
        }
        
        return result;
    }

    public Long getDuration() {
        return when.getDuration();
    }

    public void setDuration(Long newDuration) {
        when.setDuration(newDuration);
    }

    public String getDurationStr() {
        return when.getDurationStr();
    }

    public boolean isCompleted() {
        return status;
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
        str.append(getStatusStr());

        return str.toString();
    }

    public String getDisplayTaskStr() {
        StringBuilder str = new StringBuilder();

        addOutput(str, "Task:" + name, name);
        addOutput(str, "Place: " + place, place);
        addOutput(str, "Priority: " + getPriorityStr(), priority);
        addOutput(str, "Date: " + getStartEndDate(), when.getStartDateTime());
        addOutput(str, "Duration: " + getDurationStr(), when.getDurationStr());
        addOutput(str, "Deadline: " + getDeadlineShort(), when.getDeadline());

        return str.toString();
    }

    private void addOutput(StringBuilder str, String info, Object obj) {
        if (obj == null) {
            return;
        }
        str.append("\n");
        str.append(info);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        
        Task other = (Task) o;
        int result = 0;
        
        // early deadline fist
        if (result == 0) {
            result = compareDeadline(other.getDeadline());
        }
        
        // early startDate first
        if (result == 0) {
            result = compareStartDateTime(other.getStartDateTime());
        }
        
        // higher priority first
        if (result == 0) {
            result = comparePriority(other.getPriority());
        }
        
        return result;
    }
    
    public int compareName(String other) {
        return name.compareTo(other);
    }

    public int compareDeadline(Date other) {
        if (getDeadline() != null && other != null) {
            return getDeadline().compareTo(other);
        }
        
        if (getDeadline() != null) {
            return -1;
        }
        if (other != null) {
            return 1;
        }
        
        return 0;
    }
    
    public int compareStartDateTime(Date other) {
        if (getStartDateTime() != null && other != null) {
            return getStartDateTime().compareTo(other);
        }
        
        if (getStartDateTime() != null) {
            return -1;
        }
        if (other != null) {
            return 1;
        }
        
        return 0;
    }
    
    public int compareDuration(Long other) {
        if (getDuration() != null && other != null) {
            return getDuration().compareTo(other);
        }
        
        if (getDuration() != null) {
            return -1;
        }
        
        if (other != null) {
            return 1;
        }
        
        return 0;
    }
    
    public int comparePriority(Priority other) {
        return priority.compareTo(other);
    }
    
    public int compareStatus(Boolean other) {
        if (status == other) {
            return 0;
        }
        
        if (status == Task.INCOMPLETE) {
            return -1;
        }
        
        return 1;
    }

    /**
     * Function for the gCalSyn class
     * @author songyy
     * @return
     */
    public void setGCalId(String id){
    	this.gCalId = id;
    }
    
    public String getGCalId(){
    	return this.gCalId;
    }
    
	/**
	 * Needed by GCalSync
	 * @author songyy
	 * @return
	 */
	public Date getLastEditTime() {
		return this.lastEditTime;
	}
	
	/**
	 * Needed by GCalSync
	 * @author songyy
	 * @return
	 */
	public void setLastEditTime(Date time) {
		this.lastEditTime = time;
	}
	
	/**
	 * Needed by GCalSyn
	 * @return
	 */
	public String getGCalDescription(){
        StringBuilder str = new StringBuilder();

        str.append("Created by Task metter. Here's the priorities in TaskMeter cannot sync directly: \n");
        addOutput(str, "	Place: " + place, place);
        addOutput(str, "	Priority: " + getPriorityStr(), priority);
        addOutput(str, "	Duration: " + getDurationStr(), when.getDurationStr());
        addOutput(str, "	Deadline: " + getDeadlineShort(), when.getDeadline());

        return str.toString();
	}
    
    
    // used as XML tag names
    public static final String NAME       = "name";
    public static final String LIST       = "list_name";
    public static final String PLACE      = "place";
    public static final String PRIORITY   = "priority";
    public static final String START_DATE = "start_date";
    public static final String END_DATE   = "end_date";
    public static final String DEADLINE   = "deadline";
    public static final String STATUS     = "status";
    public static final String DURATION   = "duration";

}
