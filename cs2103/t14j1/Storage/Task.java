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

    // private members
    private String              name;              // define the task action
    private String              place;             // define the place of task
    private String              list;              // belong to which list
    private Priority            priority;          // priority of the task
    private When                 when;             // stores start/end, duration, deadline
    private boolean             status;            // completed or not

    public static final boolean COMPLETED  = true;
    public static final boolean INCOMPLETE = false;

    /**
     * A Constructor with all parameters provided
     */
    public Task(String name, String place, String list, Priority priority, Date startDateTime, Date endDateTime,
            Date deadline, Long duration, boolean status) {
        this.name          = name;
        this.place         = place;
        this.list          = (list == null) ? TaskLists.INBOX : list;
        this.priority      = (priority == null) ? Priority.NORMAL : priority;
        this.when          = new When(startDateTime, endDateTime, deadline, duration);
        this.status        = status;
    }
    
    /**
     * new constructor with all parameters
     */
    public Task(String name, String place, String list, Priority priority, When when, boolean status) {
        this.name          = name;
        this.place         = place;
        this.list          = (list == null) ? TaskLists.INBOX : list;
        this.priority      = (priority == null) ? Priority.NORMAL : priority;
        this.when          = (when == null) ? new When() : when;
        this.status        = status;
    }

    /**
     * A Constructor with name and list provided only
     */
    public Task() {
        this.name          = null;
        this.place         = null;
        this.list          = TaskLists.INBOX;
        this.priority      = Priority.NORMAL;
        this.when          = new When();
        this.status        = INCOMPLETE;
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