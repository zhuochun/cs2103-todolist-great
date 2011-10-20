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