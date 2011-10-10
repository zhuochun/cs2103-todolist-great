package cs2103.t14j1.storage;

import java.util.Iterator;
import java.util.List;
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

    /* Messages */
    private static final String ADD_SUCCESS    = "List \"%1$s\" is Successfully Added";
    private static final String ADD_FAIL       = "List \"%1$s\" already exists";
    private static final String REMOVE_SUCCESS = "List \"%1$s\" is Successfully Removed";
    private static final String MOVE_SUCCESS   = "Task \"%1$s\" is Successfully Moved from List \"%2$s\" to List \"%3$s\"";
    private static final String MOVE_FAIL      = "Task is not found in List \"%1$s\"";

    /* Default Lists */
    public static final String INBOX = "Inbox";
    public static final String TRASH = "Trash";

    /* constructor */
    public TaskLists() {
        lists = new TreeMap<String, TaskList>();
        // add default lists
        add(INBOX);
        add(TRASH);
    }
    
    
    /**
     * Songyy notes on 2011-10-10 23:22:02
     *  add in this method to get all the lists current have
     * @return
     */
    public String[] getListNames(){
    	Set<String> listSet = lists.keySet();
    	String arr[] = {};
    	return listSet.toArray(arr);
    	
		//return (String[]) lists.keySet().toArray();
    }
    
    /**
     * add a new list
     * 
     * @param name
     *            new list name
     * @return the result of adding a list
     */
    public String add(String name) {
        if (lists.containsKey(name)) {
            return String.format(ADD_FAIL, name);
        }

        return add(new TaskList(name));
    }

    /**
     * add an existing TaskList
     * 
     * @param list
     *            the list object
     * @return the result of adding a list
     */
    public String add(TaskList list) {
        lists.put(list.getName(), list);

        return String.format(ADD_SUCCESS, list.getName());
    }

    /**
     * delete a existing list
     * 
     * add by Songyy at 2011-10-10 23:32:50
     * 	before removing the list, one should move all the elements to Inbox -- the default list
     * 	the Inbox cannot be removed
     * @param name
     *            the list name
     * @return result of removal
     */
    public String remove(String name) {
    	if(name.compareTo(INBOX) == 0){
    		return "Inbox cannot be removed";
    	} else if(name.compareTo(TRASH) == 0){
    		return "Trash cannot be removed";
    	} else if(name == null){
    		return "No list name given";
    	} else if(!lists.containsKey(name)){
    		return "List doesn't exist";
    	}
    	
    	// before remove the list, move all the list into the inbox
    	TaskList listToRemove = lists.get(name);
    	List<Task> taskList =  listToRemove.getTasks();
    	
    	TaskList inbox = lists.get(INBOX);
    	
    	for(Task task:taskList){
    		task.setList(INBOX);
    		inbox.addTask(task);
    	}
    	
        TaskList list = lists.remove(name);
        return String.format(REMOVE_SUCCESS, list.getName());
    }

    /**
     * delete a existing list
     * 
     * @param list
     *            the list object
     * @return result of removal
     */
    public String remove(TaskList list) {
        lists.remove(list.getName());
        return String.format(REMOVE_SUCCESS, list.getName());
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
    public String addTask(String listName, Task task) {
        String result;

        if (lists.containsKey(listName)) {
            result = lists.get(listName).add(task);
        } else { // if the list does not exist, create it
            add(listName);
            result = lists.get(listName).add(task);
        }

        return result;
    }

    /**
     * remove a task from a list
     * 
     * @param listName
     *            the list which the task stays
     * @param index
     *            index of the task in list
     * @return the result of adding a list
     */
    public String deleteTask(String listName, int index) {
        return lists.get(listName).delete(index);
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
     * @return the result shown to the user
     */
    public String moveTask(String oldList, String newList, int index) {
        TaskList oldlist = getList(oldList);

        Task task = oldlist.getTask(index);

        if (task != null) {
            task.setList(newList);

            addTask(newList, task);

            oldlist.delete(index);
        } else {
            return String.format(MOVE_FAIL, oldList);
        }

        return String.format(MOVE_SUCCESS, task.getName(), oldList, newList);
    }

    /**
     * get the specific list with name passed
     * 
     * @param name
     * @return the result shown to the user
     */
    public TaskList getList(String name) {
    	if(name == null){
    		name = INBOX;
    	}
        return lists.get(name);
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