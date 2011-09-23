package cs2103.t14j1.storage;

import java.util.ArrayList;

/**
 * a basic task list and its properties
 * 
 * @author Shubham, Zhuochun
 *
 ************************************
 * update at 2011-9-23 8:30:24, by Song Yangyu:
 *   Defined the magic string for "inbox" and "trash" -- cuz it's commonly used,
 * its name should be standardized. 
 */
public class TaskList {

	private String name; // name of the list
	private ArrayList<Task> tasks;
	
	private static final String ADD_SUCCESS = "Task \"%1$s\" is Successfully Added";
	private static final String DELETE_SUCCESS = "Task \"%1$s\" is Successfully Deleted";
	private static final String INVALID_INDEX = "Invalid Index";
	
	// create some very basic List Name
	public static final String INOBX = "Inbox";
	public static final String TRASH = "Trash";
	
	public TaskList(String name) {
		this.name = name;
		tasks = new ArrayList<Task>();
	}
	
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
	
	public int getSize() {
		return tasks.size();
	}
	
	public Task getTask(int index) {
		return tasks.get(index);
	}
	
	public String getName() {
		return name;
	}
	
	public String add(Task task) {
		tasks.add(task); // no need to consider out of memory condition yet
		
		return String.format(ADD_SUCCESS, task.getName());
	}
	
	public String delete(int index) {
		if (index < 1 || index > tasks.size()) {
			return INVALID_INDEX;
		}
		
		Task task = tasks.remove(index-1);
		
		return String.format(DELETE_SUCCESS, task.getName());
	}
	
	/* leave this until smartBar related work is done
	public TaskList search(String criteria, String searchTerm) {
		
		ArrayList<Task> searchAnswers = new ArrayList<Task>();
		for(int i = 0; i < count; i ++) {
			Task task = listOfTasks.get(i);
			boolean isMatch = matchThisTask(criteria, searchTerm, task);
			if (isMatch)
				searchAnswers.add(task);
		}
		return searchAnswers;
	}
	*/

	/* leave this until smartBar related work is done
	private boolean matchThisTask(String criteria, String searchTerm, Task task) {
		String valueOfCriteriaForTask = task.getValue(criteria).toString();
		if(valueOfCriteriaForTask.equals(searchTerm))
			return true;
		else
			return false;
		return true;
	}
	*/
	
}
