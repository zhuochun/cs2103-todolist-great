package cs2103.t14j1.storage;

import java.util.ArrayList;

/**
 * @author Shubham Goyal
 *	This class is an abstraction for the category tasks are placed under.
 */
public class TaskList {
	/*
	private ArrayList<Task> listOfTasks;
	private int count;
	
	public TaskList() {
		listOfTasks =  new ArrayList<Task>();
		count = 0;
	}
	
	public boolean add(Task task) {
		
		boolean success = listOfTasks.add(task);
		if(success == true)
			count ++;
		return success;
	}

	public boolean removeTaskFromTaskList (Task task) {
		boolean success = listOfTasks.remove(task);
		if(success == true)
			count --;
		return success;
	}

	public ArrayList<Task> searchTask(String criteria, String searchTerm) {
		ArrayList<Task> searchAnswers = new ArrayList<Task>();
		for(int i = 0; i < count; i ++) {
			Task task = listOfTasks.get(i);
			boolean isMatch = matchThisTask(criteria, searchTerm, task);
			if (isMatch)
				searchAnswers.add(task);
		}
		return searchAnswers;
	}

	private boolean matchThisTask(String criteria, String searchTerm, Task task) {
		String valueOfCriteriaForTask = task.getValue(criteria).toString();
		if(valueOfCriteriaForTask.equals(searchTerm))
			return true;
		else
			return false;
	}
	
	public Task get(int index) {
		if((index > count) || (index < 1))
			return null;
		else
			return listOfTasks.get(index - 1);
	}
	
	*/
	
}
