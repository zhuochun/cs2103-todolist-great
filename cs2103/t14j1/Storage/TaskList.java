package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * a basic task list and its properties
 * 
 * @author Zhuochun
 * 
 */
public class TaskList implements Iterable<Task> {

    private String listname;  // name of the list
    private final List<Task> tasks;
    
    // Exception Strings
    private static final String EXCEPTION_LIST_EMPTY_NAME = "TaskList name cannot be empty";
    private static final String EXCEPTION_INVALID_INDEX = "Invalid index on retriving/deleting task";
    private static final String EXCEPTION_EMPTY_TASK = "Task cannot be null or empty";

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
    
    public void sort() {
        Collections.sort(tasks);
    }

    /**
     * iteration of tasks in a TaskList
     */
    public Iterator<Task> iterator() {
        Iterator<Task> iterateTasks = tasks.iterator();
        return iterateTasks;
    }
}