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
public class TaskList implements Iterable<Task> {

    private String listname;  // name of the list
    private final List<Task> tasks;

    private static final String ADD_SUCCESS    = "Task \"%1$s\" is Successfully Added";
    private static final String DELETE_SUCCESS = "Task \"%1$s\" is Successfully Deleted";
    private static final String INVALID_INDEX  = "Invalid Index";

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

    public String add(Task task) {
        tasks.add(task);
        return String.format(ADD_SUCCESS, task.getName());
    }

    public String delete(int index) {
        if (index < 1 || index > tasks.size()) {
            return INVALID_INDEX;
        }
        Task task = tasks.remove(index - 1);
        return String.format(DELETE_SUCCESS, task.getName());
    }

    /**
     * iteration of tasks in a TaskList
     */
    public Iterator<Task> iterator() {
        Iterator<Task> iterateTasks = tasks.iterator();
        return iterateTasks;
    }

    public int findIndexOfTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).equals(task)) {
                return i;
            }
        }
        return -1;
    }
}