package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * a basic task list and its properties
 * 
 * @author Zhuochun
 *
 */
public class TaskList extends AbstractModelObject implements Iterable<Task> {

	private String name; // name of the list
	private List<Task> tasks;
	
	private static final String ADD_SUCCESS    = "Task \"%1$s\" is Successfully Added";
	private static final String DELETE_SUCCESS = "Task \"%1$s\" is Successfully Deleted";
	private static final String INVALID_INDEX  = "Invalid Index";
	
	/**
	 * constructor with list name
	 */
	public TaskList(String name) {
		this.name = name;
		tasks = new ArrayList<Task>();
	}
	
	public int getSize() {
    	return tasks.size();
    }

    public boolean isEmpty() {
    	return tasks.isEmpty();
    }

    public String getName() {
		return name;
	}
	
	/**
	 * set the list name
	 * 
	 * @param newName
	 */
	public void setName(String newName) {
	    String oldName = name;
	    name = newName;
	    firePropertyChange("name", oldName, name);
	}
	
	public Task getTask(int index) {
    	return tasks.get(index);
    }

    public List<Task> getTasks() {
        return tasks;
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
	
	/**
	 * iteration of tasks in a TaskList
	 */
	public Iterator<Task> iterator() {
	    Iterator<Task> iterateTasks = tasks.iterator();
	    return iterateTasks;
	}
	
}
