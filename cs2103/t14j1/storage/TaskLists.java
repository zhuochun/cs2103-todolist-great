package cs2103.t14j1.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Stores all the list, need this extra abstraction for GUI
 * 
 * @author Zhuochun
 *
 */
public class TaskLists extends AbstractModelObject implements Iterable<Entry<String, TaskList>> {
	
	/*
     * Zhuochun's note:
	 *  Use tree because we need to use list name frequently
	 *
	 * Songyy's note:
	 *  Another good thing of using a tree, instead of hash table, is that it's 
	 *  possible to traverse the tree, but impossible for the hash table
	 */
	private TreeMap<String, TaskList> lists;
	private List<TaskList> m_lists; // it seems that data binding only support List
	
	/* Messages */
	private static final String ADD_SUCCESS = "List \"%1$s\" is Successfully Added";
	private static final String ADD_FAIL = "List \"%1$s\" already exists";
	private static final String REMOVE_SUCCESS = "List \"%1$s\" is Successfully Removed";

    /* Default Lists */
	public static final String INBOX = "Inbox";
	public static final String TRASH = "Trash";
	
	/* constructor */
	public TaskLists() {
		lists = new TreeMap<String, TaskList>();
		m_lists = new ArrayList<TaskList>();
		// add default lists
		add(INBOX);
		add(TRASH);
	}
	
	/**
	 * add a new list
	 * 
	 * @param name				new list name
	 * @return
	 */
	public String add(String name) {
	    if (lists.containsKey(name)) {
	        return String.format(ADD_FAIL, name);
	    }
	    
	    return addList(new TaskList(name));
	}

	/**
	 * add an existing tasklist
	 * 
	 * @param list
	 * @return
	 */
	public String addList(TaskList list) {
	    lists.put(list.getName(), list);
	    m_lists.add(list);
	    
	    firePropertyChange("lists", null, m_lists);
	    
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
		
		// remove from m_lists, efficient way?
		
	    firePropertyChange("lists", null, m_lists);
		
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
		} else { // if the task does not exist, create it
			add(listName); 
			result = lists.get(listName).add(task);
		}
		
		return result;
	}
	
	/**
	 * edit a task by replace the original task with the new task passed in
	 * 
	 * @param listName			the list of task stays
	 * @param task				the new task used to replace old task
	 * @return
	 */
	public String editTask(String listName, Task task) {
		
		// TODO: left this function for version 0.2, because this method is depreciated
		
		return null;
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

	public List<TaskList> getLists() {
	    return m_lists;
	}
	
	/**
	 * iteration of lists in TaskLists
	 */
    public Iterator<Entry<String, TaskList>> iterator() {
        Iterator<Entry<String, TaskList>> iterateList = lists.entrySet().iterator();
        return iterateList;
    }
    
}
