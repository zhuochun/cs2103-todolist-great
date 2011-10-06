package cs2103.t14j1.storage;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Stores all the list, need this extra abstraction for GUI
 * 
 * @author Zhuochun
 *
 */
public class TaskLists extends AbstractModelObject implements Iterable<Entry<String, TaskList>> {
	
	private TreeMap<String, TaskList> lists;
	
	/* Messages */
	private static final String ADD_SUCCESS = "List \"%1$s\" is Successfully Added";
	private static final String ADD_FAIL = "List \"%1$s\" already exists";
	private static final String REMOVE_SUCCESS = "List \"%1$s\" is Successfully Removed";
	private static final String MOVE_SUCCESS = "Task \"%1$s\" is Successfully Moved from List \"%2$s\" to List \"%3$s\"";
	private static final String MOVE_FAIL = "Task is not found in List \"%1$s\"";

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
	 * add a new list
	 * 
	 * @param name 					new list name
	 * @return
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
	 * @return
	 */
	public String add(TaskList list) {
	    lists.put(list.getName(), list);
	    
	    firePropertyChange("lists", null, lists);
	    
		return String.format(ADD_SUCCESS, list.getName());
	}
	
	/**
	 * delete a existing list
	 * 
	 * @param name
	 * @return
	 */
	public String remove(String name) {
		TaskList list = lists.remove(name);
		
	    firePropertyChange("lists", null, lists);
		
		return String.format(REMOVE_SUCCESS, list.getName());
	}
	
	/**
	 * add a task into list
	 * 
	 * @param listName			the list name of the task will be added to
	 * @param task				the task object
	 * @return
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
	 * @param listName         the list which the task stays
	 * @param index            index of the task in list
	 * @return
	 */
	public String deleteTask(String listName, int index) {
	    return lists.get(listName).delete(index);
	}
	
	/**
	 * move a task from old list to new list
	 * 
	 * @param oldList          the old list name
	 * @param newList          the new list name
	 * @param index            index of the task in old list
	 * @return
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
	 * @return
	 */
	public TaskList getList(String name) {
		return lists.get(name);
	}

	/**
	 * iteration of lists in TaskLists
	 */
    public Iterator<Entry<String, TaskList>> iterator() {
        Iterator<Entry<String, TaskList>> iterateList = lists.entrySet().iterator();
        return iterateList;
    }

}