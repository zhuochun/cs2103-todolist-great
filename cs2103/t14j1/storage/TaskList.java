package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cs2103.t14j1.storage.gCal.GCalSyn;

/**
 * a basic task list and its properties
 * 
 * @author Zhuochun
 * 
 */
public class TaskList implements Iterable<Task> {

    private String listname;  // name of the list
    private final List<Task> tasks;
    
    
    // properties used by gCalSyn
    private String gCalId = null;
    private int gCalProperty = GCalSyn.NOT_SYN;
    private String colour = null;

    /**
     * constructor with list name
     */
    public TaskList(String name) {
        this.listname = name;
        tasks = new ArrayList<Task>();
    }

    public int getSize() {
        return tasks.size();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    public String getName() {
        return listname;
    }

    /**
     * set the list name or rename the list name
     * 
     * @param newName
     */
    public void setName(String newName) {
        listname = newName;

        // change all task in lists
        for (Task i : tasks) {
            i.setList(listname);
        }
    }

    public Task getTask(int index) {
        if (index < 1 || index > tasks.size()) {
            return null;
        }

        return tasks.get(index - 1);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean addTask(Task task) {
        return tasks.add(task);
    }
    
    public boolean removeTask(Task task) {
        return tasks.remove(task);
    }

    public Task removeTask(int index) {
        if (index < 1 || index > tasks.size()) {
            return null;
        }
        return tasks.remove(index - 1);
    }
    
    public int getIndexOfTask(Task task) {
        int i = 1;
        for (Task t : tasks) {
            if (t.equals(task)) {
                return i;
            }
            
            i++;
        }
        return -1;
    }

    /**
     * iteration of tasks in a TaskList
     */
    public Iterator<Task> iterator() {
        Iterator<Task> iterateTasks = tasks.iterator();
        return iterateTasks;
    }
    
    
    // method created for gCalSyn
    public void setGCalId(String id){
    	this.gCalId = id;
    	// the only reason for setting the id is because it's just created
    	// therefore the status should be set to "updated"
    	this.gCalProperty = GCalSyn.UPDATED;
    }
    
    public String getGCalId(){
    	return this.gCalId;
    }
    
    public boolean setGCalProperty(int gCalProperty){
    	if(this.gCalProperty == gCalProperty){
    		return false;
    	}
    	this.gCalProperty = gCalProperty;
    	return true;
    }
    
    public int getGCalProperty(){
    	return this.gCalProperty;
    }
    
    public void setListColourStr(String colour){
    	this.colour = colour;
    }
    
    public String getListColourStr(){
    	return this.colour;
    }
}