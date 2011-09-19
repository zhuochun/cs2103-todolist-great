package cs2103.t14j1.Storage;

import java.util.ArrayList;

/**
 * @author Shubham Goyal
 *	This class is an abstraction for the category tasks are placed under.
 */
public class TaskList {
	private ArrayList<Task> listOfTasks;
	private int numberOfTasks;
	
	public TaskList() {
		listOfTasks =  new ArrayList<Task>();
		numberOfTasks = 0;
	}
	
	public boolean addTaskIntoTaskList(Task task) {
		
		boolean success = listOfTasks.add(task);
		incrementNumberOfTaksIfTaskSuccessfullyAddd(success);
		return success;
	}
	
	private void incrementNumberOfTaksIfTaskSuccessfullyAddd(boolean success) {
		if(success == true)
			numberOfTasks ++;
	}

	public boolean removeTaskFromTaskList (Task task) {
		boolean success = listOfTasks.remove(task);
		decrementNumberOfTaksIfTaskSuccessfullyRemoved(success);
		return success;
	}
	
	private void decrementNumberOfTaksIfTaskSuccessfullyRemoved(boolean success) {
		if(success == true)
			numberOfTasks --;
	}

	public ArrayList<Task> searchTask(String criteria, String searchTerm) {
		ArrayList<Task> searchAnswers = new ArrayList<Task>();
		for(int i = 0; i < numberOfTasks; i ++) {
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
}
