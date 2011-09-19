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
	private int priority; //Can be any of 1, 2 or 3
	private long deadline;
	private long startDate;
	private long startTime;
	private long endDate;
	private long endTime;
	private String place;
	
	public Task (String whatToDo, Integer priority, Long deadline, Long startDate,
			Long startTime, Long endDate, Long endTime, String place) {
	
		/*null checks are introduced because primitive data types like long and int
		can't be assigned the value null*/
		
		this.whatToDo = whatToDo;
		if(priority != null)
			this.priority = priority;
		if(deadline != null)
			this.deadline = deadline;
		if(startDate != null)
			this.startDate = startDate;
		if(startTime != null)
			this.startTime = startTime;
		if(endDate != null)
			this.endDate = endDate;
		if(endTime != null)
			this.endTime = endTime;
		this.place = place;
	}
	
	public Object getValue(String nameOfParameter) {
		if (areStringsEqual(nameOfParameter, "priority"))
			return priority;
		else if (areStringsEqual(nameOfParameter, "whatToDo"))
			return whatToDo;
		else if (areStringsEqual(nameOfParameter, "deadline"))
			return deadline;
		else if (areStringsEqual(nameOfParameter, "startDate"))
			return startDate;
		else if (areStringsEqual(nameOfParameter, "startTime"))
			return startTime;
		else if (areStringsEqual(nameOfParameter, "endDate"))
			return endDate;
		else if (areStringsEqual(nameOfParameter, "endTime"))
			return endTime;
		else if (areStringsEqual(nameOfParameter, "place"))
			return place;
		else {
			System.err.println("Error: Trying to get value of a parameter which" +
					"doesn't exist");
			return null;
		}
	}
	
	private boolean areStringsEqual(String a, String b) {
		return a.equals(b);
	}
}
