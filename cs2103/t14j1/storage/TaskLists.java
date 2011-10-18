package cs2103.t14j1.storage;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Stores all the list, need this extra abstraction for GUI
 * 
 * @author Zhuochun
 * 
 */
public class TaskLists implements Iterable<Entry<String, TaskList>> {

    private TreeMap<String, TaskList> lists;

    /* Default Lists */
    public static final String INBOX = "Inbox";
    public static final String TRASH = "Trash";

    /* constructor */
    public TaskLists() {
        lists = new TreeMap<String, TaskList>();
        // add default lists
        addList(INBOX);
        addList(TRASH);
    }
    
    /**
     * add a new list
     * 
     * @param name
     *            new list name
     * @return the result of adding a list
     */
    public boolean addList(String name) {
        if (lists.containsKey(name)) {
            return false;
        } else if (name.equals(INBOX)) {
            return false;
        } else if (name.equals(TRASH)) {
            return false;
        }
        
        lists.put(name, new TaskList(name));
        return true;
    }

    /**
     * add an existing TaskList
     * 
     * @param list
     *            the list object
     * @return the result of adding a list
     */
    public boolean addList(TaskList list) {
        if (lists.containsKey(list.getName())) {
            return false;
        } else if (list.getName().equals(INBOX)) {
            return false;
        } else if (list.getName().equals(TRASH)) {
            return false;
        }
        
        lists.put(list.getName(), list);
        return true;
    }

    /**
     * 
     * @param name
     *            the list name
     * @return result of removal
     */
    public TaskList removeList(String name) {
        if (name.equals(INBOX) || name.equals(TRASH)) {
            return null;
        }
        
        return lists.remove(name);
    }

    /**
     * delete a existing list
     * 
     * @param list
     *            the list object
     * @return the TaskList deleted
     */
    public TaskList removeList(TaskList list) {
        if (list.getName().equals(INBOX) || list.getName().equals(TRASH)) {
            return null;
        }
        
        return lists.remove(list.getName());
    }

    /**
     * add a task into list
     * 
     * @param listName
     *            the list name of the task will be added to
     * @param task
     *            the task object
     * @return the result of adding a list
     */
    public boolean addTask(String listName, Task task) {
        if (lists.containsKey(listName)) {
            return lists.get(listName).addTask(task);
        } else { // if the list does not exist, create it
            addList(listName);
            return lists.get(listName).addTask(task);
        }
    }

    /**
     * remove a task from a list
     * 
     * @param listName
     *            the list which the task stays
     * @param index
     *            index of the task in list
     * @return the task removed
     */
    public Task removeTask(String listName, int index) {
        return lists.get(listName).removeTask(index);
    }

    /**
     * remove a task from a list
     * 
     * @param listName
     *            the list which the task stays
     * @param task
     *            the task object
     * @return the result of adding a list
     */
    public boolean removeTask(String listName, Task task) {
        return lists.get(listName).removeTask(task);
    }

    /**
     * move a task from old list to new list
     * 
     * @param oldList
     *            the old list name
     * @param newList
     *            the new list name
     * @param index
     *            index of the task in old list
     * @return the result of moving task
     */
    public boolean moveTask(String oldList, String newList, int index) {
        TaskList oldlist = getList(oldList);
        Task task = oldlist.getTask(index);

        if (task != null) {
            task.setList(newList);
            addTask(newList, task);
            oldlist.removeTask(index);
        } else {
            return false;
        }

        return true;
    }

    /**
     * get the specific list with name passed
     * 
     * @param name
     * @return the TaskList with the name provided
     */
    public TaskList getList(String name) {
        return lists.get(name);
    }

    /**
     * @return an array of all the list names
     */
    public String[] getListNames(){
    	Set<String> listSet = lists.keySet();
    	String arr[] = {};
    	return listSet.toArray(arr);
    }


    /**
     * check whether the list name already exists
     * 
     * @param name
     * @return the result shown to the user
     */
    public boolean hasList(String name) {
        return lists.containsKey(name);
    }

    /**
     * iteration of lists in TaskLists
     */
    public Iterator<Entry<String, TaskList>> iterator() {
        Iterator<Entry<String, TaskList>> iterateList = lists.entrySet().iterator();
        return iterateList;
    }

}