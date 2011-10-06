package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * a basic task list and its properties
 * 
 * @author Shubham, Zhuochun
 *
 */
public class TaskList extends AbstractModelObject implements Iterable<Task> {

	private String name; // name of the list
	private List<Task> tasks;
	
	private static final String ADD_SUCCESS = "Task \"%1$s\" is Successfully Added";
	private static final String DELETE_SUCCESS = "Task \"%1$s\" is Successfully Deleted";
	private static final String INVALID_INDEX = "Invalid Index";
	
	public TaskList(String name) {
		this.name = name;
		tasks = new ArrayList<Task>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
	    String oldName = name;
	    name = newName;
	    firePropertyChange("listname", oldName, name);
	}
	
	public String add(Task task) {
		tasks.add(task);
		
		firePropertyChange("tasks", null, tasks);
		
		return String.format(ADD_SUCCESS, task.getName());
	}
	
	public String delete(int index) {
		if (index < 1 || index > tasks.size()) {
			return INVALID_INDEX;
		}
		
		Task task = tasks.remove(index-1);
		
        firePropertyChange("tasks", null, tasks);
        
		return String.format(DELETE_SUCCESS, task.getName());
	}
	
	public Task getTask(int index) {
    	return tasks.get(index);
    }
	
	public int getSize() {
    	return tasks.size();
    }

    public boolean isEmpty() {
    	return tasks.isEmpty();
    }

    public List<Task> getTasks() {
        return tasks;
    }
    
    /**
	 * iteration of tasks in a TaskList
	 */
	public Iterator<Task> iterator() {
	    Iterator<Task> iterateTasks = tasks.iterator();
	    return iterateTasks;
	}
	
}
