package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cs2103.t14j1.storage.gCal.GCalSyn;

/**
 * a basic task list and its properties
 * 
 * @author Zhuochun, Shusbham
 * 
 */
public class TaskList implements Iterable<Task> {

    private String           listname;  // name of the list
    private final List<Task> tasks;
    
    // properties used by gCalSyn
    private String gCalId    = null;
    private int gCalProperty = GCalSyn.NOT_SYN;
    private String colour    = null;

    // Exception Strings
    private static final String EXCEPTION_LIST_EMPTY_NAME = "TaskList name cannot be empty";
    private static final String EXCEPTION_INVALID_INDEX   = "Invalid index on retriving/deleting task";
    private static final String EXCEPTION_EMPTY_TASK      = "Task cannot be null or empty";
    
    // sort terms
    public static final int SORT_ID       = 0;
    public static final int SORT_NAME     = 1;
    public static final int SORT_PRIORITY = 2;
    public static final int SORT_DATE     = 3;
    public static final int SORT_DEADLINE = 4;
    public static final int SORT_DURATION = 5;
    public static final int SORT_STATUS   = 6;

    /**
     * constructor with list name
     */
    public TaskList(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_LIST_EMPTY_NAME);
        }

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
    
    public void clear() {
        tasks.clear();
    }

    /**
     * set the list name or rename the list name
     * 
     * @param newName
     */
    public void setName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_LIST_EMPTY_NAME);
        }

        listname = newName;

        // change all tasks in this list
        for (Task i : tasks) {
            i.setList(listname);
        }
    }

    /**
     * index start from 1
     * 
     * @param index
     * @return
     */
    public Task getTask(int index) {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfBoundsException(EXCEPTION_INVALID_INDEX);
        }

        return tasks.get(index - 1);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean addTask(Task task) {
        if (task == null || task.getName().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_TASK);
        }
        
        return tasks.add(task);
    }
    
    public boolean removeTask(Task task) {
        if (task == null || task.getName().trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_TASK);
        }
        
        return tasks.remove(task);
    }
    
    public boolean hasTask(Task task) {
        return tasks.contains(task);
    }

    /**
     * index start from 1
     * 
     * @param index
     * @return
     */
    public Task removeTask(int index) {
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfBoundsException(EXCEPTION_INVALID_INDEX);
        }
        return tasks.remove(index - 1);
    }
    
    public int getIndexOfTask(Task task) {
        if (task == null || task.getName().trim().isEmpty()) {
            throw new NullPointerException(EXCEPTION_EMPTY_TASK);
        }
        
        int i = 1;
        for (Task t : tasks) {
            if (t.equals(task)) { return i; }
            i++;
        }
        return -1;
    }
    
    /**
     * sort according to default sort
     */
    public void sort() {
        Collections.sort(tasks);
    }
    
    public void sort(int col) {
        switch (col) {
            case SORT_ID:
                sort();
                break;
            case SORT_NAME:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.compareName(b.getName());
                    }
                });
                break;
            case SORT_PRIORITY:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.comparePriority(b.getPriority());
                    }
                });
                break;
            case SORT_DATE:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.compareStartDateTime(b.getStartDateTime());
                    }
                });
                break;
            case SORT_DEADLINE:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.compareDeadline(b.getDeadline());
                    }
                });
                break;
            case SORT_DURATION:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.compareDuration(b.getDuration());
                    }
                });
                break;
            case SORT_STATUS:
                Collections.sort(tasks, new Comparator<Task>() {
                    public int compare(Task a, Task b) {
                        return a.compareStatus(b.getStatus());
                    }
                });
                break;
            default:
                sort();
                break;
        }
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
