package cs2103.t14j1.Storage;

/**
 * @author Shubham Goyal
 * This class defines a task after it has been loaded from storage
 * @Worapol - I decided that since I am implementing Control, I need to depend on Task
 * and TaskList a lot. So, please implement FileHandler which should be quite tough really
 * and leave these 2 esy psys to me.
 */
public class Task {
	
	private String whatToDo;
	private Priority priority; //Can be any of 1, 2 or 3
	private long deadline;
	private long startDate;
	private long startTime;
	private long endDate;
	private long endTime;
	private String place;
	
	public Task (String whatToDo, Priority priority, Long deadline, Long startDate,
			Long startTime, Long endDate, Long endTime, String place) {
		
		this.whatToDo = whatToDo;
		this.priority = priority;
		this.deadline = deadline;
		this.startDate = startDate;
		this.startTime = startTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.place = place;
	}
	
	public String getWhatToDo() {
		return whatToDo;
	}
	
	public Priority getPriority() {
		return priority;
	}
	
	public long getDeadline() {
		return deadline;
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
	
	public String getPlace() {
		return place;
	}
	
	private boolean areStringsEqual(String a, String b) {
		return a.equals(b);
	}
}
