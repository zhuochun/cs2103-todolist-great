package cs2103.t14j1.storage;

public class Task {

	private String name; // define the task action
	private String list; // belong to which list
	private Priority priority;
	private long startDate;
	private long startTime;
	private long endDate;
	private long endTime;
	private boolean status; // completed or not
	
	public static final boolean COMPLETED = true;
	public static final boolean NOT_COMPLETED = false;
	
	/**
	 * A Constructor with all parameters provided
	 */
	public Task (String name, String list, Priority priority, Long startDate, Long startTime,
			Long endDate, Long endTime, boolean status) {
		this.name      = name;
		this.list      = list;
		this.priority  = priority;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate   = endDate;
		this.endTime   = endTime;
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
	
	public long getStartDate() {
		return startDate;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEndDate() {
		return endDate;
	}
	
	public long getTime() {
		return endTime;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean newStatus) {
		status = newStatus;
	}
	
	// TODO merge startDate and startTime
	public long getStartDateTime() {
		return startDate;
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